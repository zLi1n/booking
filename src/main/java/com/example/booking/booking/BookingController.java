package com.example.booking.booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booking.answer.Answer;
import com.example.booking.answer.AnswerService;
import com.example.booking.date_answer.DateAnswer;
import com.example.booking.detail.Detail;
import com.example.booking.detail.DetailService;
import com.example.booking.question.Question;
import com.example.booking.question.QuestionService;
import com.example.booking.time_answer.TimeSlot;
import com.example.booking.user.User;
import com.example.booking.user.UserService;
import com.example.booking.user_answer.UserAnswer;
import com.example.booking.user_select.UserSelect;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private DetailService detailService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingValidationService bookingValidationService;

    @GetMapping("/main")
    public String bookingMain(@RequestParam Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "bookingMain";
    }

    @GetMapping("/book")
    public String bookNow(@RequestParam Long userId, Model model) {
        User user = userService.getUserById(userId);
        Booking booking = bookingService.createBooking(user);
        return startBooking(booking.getbId(), model);
    }

    @GetMapping("/start")
    public String startBooking(@RequestParam Long bookingId, Model model) {
        Question firstQuestion = questionService.getFirstLevelQuestion();
        if (firstQuestion == null) {
            return "redirect:/questions/add";
        }
        return showQuestion(bookingId, firstQuestion.getqId(), model);
    }

    @GetMapping("/question/{id}")
    public String showQuestion(@RequestParam Long bookingId, @PathVariable Long id, Model model) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            model.addAttribute("question", question);
            model.addAttribute("bookingId", bookingId);

            if ("text".equals(question.getType())) {
                model.addAttribute("isTextQuestion", true);
                model.addAttribute("userAnswer", new UserAnswer());
            } else {
                List<Answer> allAnswers = answerService.getAnswersByQuestion(id);
                List<Answer> availableAnswers = allAnswers.stream()
                        .filter(answer -> bookingValidationService.isAvailableAnswer(bookingId, answer))
                        .collect(Collectors.toList());
                model.addAttribute("availableAnswers", availableAnswers);
                List<Map<String, Object>> serializedAnswers = availableAnswers.stream()
                        .map(answer -> {
                            Map<String, Object> serializedAnswer = new HashMap<>();
                            serializedAnswer.put("aId", answer.getaId());
                            if (answer instanceof DateAnswer) {
                                DateAnswer dateAnswer = (DateAnswer) answer;
                                serializedAnswer.put("date", dateAnswer.getFormattedDate());
                            } else if (answer instanceof TimeSlot) {
                                TimeSlot timeSlot = (TimeSlot) answer;
                                serializedAnswer.put("timeRange", timeSlot.getFormattedTimeRange());
                            }
                            return serializedAnswer;
                        })
                        .collect(Collectors.toList());
                if (availableAnswers.isEmpty()) {
                    Answer nextAvailableAnswer = findNextAvailableAnswer(bookingId, question);
                    if (nextAvailableAnswer != null && nextAvailableAnswer.getNextQuestion() != null) {
                        return "redirect:/booking/question/" + nextAvailableAnswer.getNextQuestion().getqId()
                                + "?bookingId=" + bookingId;
                    } else {
                        return "redirect:/booking/confirmation?bookingId=" + bookingId;
                    }
                }
                model.addAttribute("isTextQuestion", false);
                model.addAttribute("availableAnswers", availableAnswers);
                model.addAttribute("serializedAnswers", serializedAnswers);
            }
            return "viewQuestionUser";
        } else {
            return "redirect:/booking/confirmation?bookingId=" + bookingId;
        }
    }

    private Answer findNextAvailableAnswer(Long bookingId, Question question) {
        List<Answer> answers = answerService.getAnswersByQuestion(question.getqId());
        for (Answer answer : answers) {
            if (bookingValidationService.isAvailableAnswer(bookingId, answer)) {
                return answer;
            }
            if (answer.getNextQuestion() != null) {
                Answer nextAvailable = findNextAvailableAnswer(bookingId, answer.getNextQuestion());
                if (nextAvailable != null) {
                    return nextAvailable;
                }
            }
        }
        return null;
    }

    @Transactional
    @PostMapping("/answer")
    public String handleAnswer(@RequestParam Long bookingId, @RequestParam Long questionId,
            @RequestParam(required = false) Long selectedAnswerId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String textAnswer,
            RedirectAttributes redirectAttributes) {
        try {
            Question question = questionService.getQuestionById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            Booking booking = bookingService.getBookingById(bookingId);

            if ("text".equals(question.getType())) {
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setBooking(booking);
                userAnswer.setQuestion(question);
                userAnswer.setText(textAnswer);
                detailService.saveDetail(userAnswer);

                System.out.println("Saved UserAnswer: " + userAnswer.getdId() + ", Text: " + userAnswer.getText());

                // Check for next question after saving text answer
                List<Question> childQuestions = questionService.getChildQuestions(questionId);
                if (!childQuestions.isEmpty()) {
                    return "redirect:/booking/question/" + childQuestions.get(0).getqId() + "?bookingId=" + bookingId;
                }
            } else {
                if (selectedAnswerId == null) {
                    throw new RuntimeException("No answer selected for non-text question");
                }
                int requestedQuantity = (quantity != null) ? quantity : 1;
                boolean isValid = bookingValidationService.validateAndUpdateQuantity(bookingId, selectedAnswerId,
                        requestedQuantity);
                if (!isValid) {
                    throw new RuntimeException(
                            "This option is no longer available or the requested quantity exceeds the limit.");
                }

                Answer answer = answerService.getAnswerById(selectedAnswerId)
                        .orElseThrow(() -> new RuntimeException("Answer not found"));

                UserSelect userSelect = new UserSelect();
                userSelect.setBooking(bookingService.getBookingById(bookingId));
                userSelect.setQuestion(question);
                userSelect.setAnswer(answer);
                userSelect.setQty(requestedQuantity);
                detailService.saveDetail(userSelect);

                if (answer.getNextQuestion() != null) {
                    Question nextQuestion = answer.getNextQuestion();
                    if ("text".equals(nextQuestion.getType())) {
                        return "redirect:/booking/question/" + nextQuestion.getqId() + "?bookingId=" + bookingId;
                    } else {
                        List<Answer> nextAnswers = answerService.getAnswersByQuestion(nextQuestion.getqId());
                        boolean hasAvailableNextAnswers = nextAnswers.stream()
                                .anyMatch(nextAnswer -> bookingValidationService.isAvailableAnswer(bookingId,
                                        nextAnswer));

                        if (hasAvailableNextAnswers) {
                            return "redirect:/booking/question/" + nextQuestion.getqId() + "?bookingId=" + bookingId;
                        }
                    }
                }
            }

            return "redirect:/booking/confirmation?bookingId=" + bookingId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/question/" + questionId + "?bookingId=" + bookingId;
        }
    }

    @GetMapping("/history")
    public String viewHistory(@RequestParam Long userId, Model model) {
        User user = userService.getUserById(userId);
        List<Booking> bookings = bookingService.getBookingsByUser(user);

        for (Booking booking : bookings) {
            List<Detail> details = detailService.getDetailsByBooking(booking);
            booking.setDetails(details);
        }

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        return "history";
    }

    @GetMapping("/confirmation")
    public String showConfirmation(@RequestParam Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        User user = booking.getUser();

        List<Detail> details = detailService.getDetailsByBooking(booking);
        System.out.println("Number of details for booking " + booking.getbId() + ": " + details.size());
        booking.setDetails(details);

        model.addAttribute("booking", booking);
        model.addAttribute("user", user);
        return "confirmation";
    }

}
