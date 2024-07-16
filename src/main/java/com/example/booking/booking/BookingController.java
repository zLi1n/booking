package com.example.booking.booking;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.booking.answer.Answer;
import com.example.booking.answer.AnswerService;
import com.example.booking.detail.Detail;
import com.example.booking.detail.DetailService;
import com.example.booking.detail.DetailService.DetailType;
import com.example.booking.question.Question;
import com.example.booking.question.QuestionService;
import com.example.booking.user.User;
import com.example.booking.user.UserService;
import com.example.booking.user_answer.UserAnswer;
import com.example.booking.user_select.UserSelect;

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
            return "viewQuestionUser";
        } else {
            return "redirect:/booking/confirmation?bookingId=" + bookingId;
        }
    }

    @PostMapping("/answer")
    public String handleAnswer(@RequestParam Long bookingId, @RequestParam Long questionId,
            @RequestParam Long selectedAnswerId) {
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        Answer answer = answerService.getAnswerById(selectedAnswerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // Create a UserAnswer or UserSelect
        Detail detail;
        if ("text".equals(question.getType())) {
            detail = detailService.createDetail(DetailType.USER_ANSWER);
            ((UserAnswer) detail).setText("Your answer text"); // Set UserAnswer specific fields
        } else {
            detail = detailService.createDetail(DetailType.USER_SELECT);
            ((UserSelect) detail).setAnswer(answer);
        }
        detail.setBooking(bookingService.getBookingById(bookingId));
        detail.setQuestion(question);
        detailService.saveDetail(detail);

        if (answer.getNextQuestion() != null) {
            return "redirect:/booking/question/" + answer.getNextQuestion().getqId() + "?bookingId=" + bookingId;
        } else {
            return "redirect:/booking/confirmation?bookingId=" + bookingId;
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
