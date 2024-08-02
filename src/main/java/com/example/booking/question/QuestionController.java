package com.example.booking.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.booking.answer.Answer;
import com.example.booking.answer.AnswerService;
import com.example.booking.choice_answer.ChoiceAnswer;
import com.example.booking.date_answer.DateAnswer;
import com.example.booking.form.Form;
import com.example.booking.form.FormService;
import com.example.booking.number_answer.NumberAnswer;
import com.example.booking.time_answer.TimeSlot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private FormService formService;

    @GetMapping("/add")
    public String addQuestionForm(@RequestParam(required = false) Long formId, Model model) {
        model.addAttribute("question", new Question());
        model.addAttribute("formId", formId);
        return "addQuestion";
    }

    @PostMapping("/add")
    public String addQuestion(@ModelAttribute Question question, @RequestParam(required = false) Long formId) {
        if (question.getParent() == null) {
            question.setLevel(1);
        }
        Question savedQuestion = questionService.saveQuestion(question);

        if (formId != null) {
            Form form = formService.getFormById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
            form.setFirstQuestionId(savedQuestion.getqId());
            formService.saveForm(form);
        }

        if ("text".equals(savedQuestion.getType())) {
            Answer textAnswer = new Answer();
            textAnswer.setQuestion(savedQuestion);
            textAnswer.setQty(null);
            answerService.createAnswer(textAnswer);
        }
        return "redirect:/questions/" + savedQuestion.getqId();
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionService.getQuestionWithAnswers(id);
            model.addAttribute("question", question);
            Optional<Question> parentQuestionOpt = questionService.getParentQuestion(id);
            parentQuestionOpt.ifPresent(parent -> model.addAttribute("parentQuestion", parent));

            if ("text".equals(question.getType()) && question.getAnswers().isEmpty()) {
                Answer textAnswer = new Answer();
                textAnswer.setQuestion(question);
                textAnswer.setQty(null);
                answerService.createAnswer(textAnswer);
                return "redirect:/questions/" + id;
            }

            if ("choice".equals(question.getType())) {
                model.addAttribute("newAnswer", new ChoiceAnswer());
            } else if ("date".equals(question.getType())) {
                model.addAttribute("newAnswer", new DateAnswer());
            } else if ("time".equals(question.getType())) {
                model.addAttribute("newAnswer", new TimeSlot());
            } else if ("number".equals(question.getType())) {
                model.addAttribute("newAnswer", new NumberAnswer());
            } else {
                model.addAttribute("newAnswer", new Answer());
            }
            Form form = formService.getFormByQuestionId(id);
            if (form != null) {
                model.addAttribute("form", form);
            }
            return "viewQuestion";
        } else {
            return "redirect:/questions";
        }
    }

    @PostMapping("/{id}/answers")
    public String addAnswer(@PathVariable Long id, @ModelAttribute Answer answer,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<String> dates,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer maxNum,
            @RequestParam(required = false) Integer minNum) throws ParseException {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            answer.setQuestion(question);

            if ("choice".equals(question.getType())) {
                ChoiceAnswer choiceAnswer = new ChoiceAnswer();
                choiceAnswer.setText(text);
                choiceAnswer.setQty(answer.getQty());
                choiceAnswer.setQuestion(question);
                if (choiceAnswer.getQty() == null || choiceAnswer.getQty() == 0) {
                    choiceAnswer.setQty(null);
                    choiceAnswer.setRemainingQty(null);
                } else {
                    choiceAnswer.setRemainingQty(choiceAnswer.getQty());
                }
                answerService.createAnswer(choiceAnswer);
            } else if ("date".equals(question.getType())) {
                if (dates != null && !dates.isEmpty()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    for (String dateStr : dates) {
                        DateAnswer dateAnswer = new DateAnswer();
                        dateAnswer.setDate(format.parse(dateStr));
                        dateAnswer.setQty(answer.getQty());
                        dateAnswer.setQuestion(question);
                        if (dateAnswer.getQty() == null || dateAnswer.getQty() == 0) {
                            dateAnswer.setQty(null);
                            dateAnswer.setRemainingQty(null);
                        } else {
                            dateAnswer.setRemainingQty(dateAnswer.getQty());
                        }
                        answerService.createAnswer(dateAnswer);
                    }
                }
            } else if ("time".equals(question.getType())) {
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setQuestion(question);
                timeSlot.setQty(answer.getQty());

                if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    try {
                        timeSlot.setStartTime(timeFormat.parse(startTime));
                        timeSlot.setEndTime(timeFormat.parse(endTime));
                    } catch (ParseException e) {
                        return "redirect:/questions/" + id + "?error=invalid_time_format";
                    }
                }

                if (timeSlot.getQty() == null || timeSlot.getQty() == 0) {
                    timeSlot.setQty(null);
                    timeSlot.setRemainingQty(null);
                } else {
                    timeSlot.setRemainingQty(timeSlot.getQty());
                }

                answerService.createAnswer(timeSlot);
            } else if ("number".equals(question.getType())) {
                NumberAnswer numberAnswer = new NumberAnswer();
                numberAnswer.setQuestion(question);
                numberAnswer.setMaxNum(maxNum);
                numberAnswer.setMinNum(minNum);
                numberAnswer.setQty(answer.getQty());
                numberAnswer.setQuestion(question);
                if (numberAnswer.getQty() == null || numberAnswer.getQty() == 0) {
                    numberAnswer.setQty(null);
                    numberAnswer.setRemainingQty(null);
                } else {
                    numberAnswer.setRemainingQty(numberAnswer.getQty());
                }
                answerService.createAnswer(numberAnswer);
            } else {
                answerService.createAnswer(answer);
            }

            return "redirect:/questions/" + id;
        } else {
            return "redirect:/questions";
        }
    }

    @PostMapping("/answers/{answerId}/nextquestion")
    public String addNextQuestion(@PathVariable Long answerId, @ModelAttribute Question nextQuestion) {
        Answer answer = answerService.getAnswerById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        Question parentQuestion = answer.getQuestion();
        nextQuestion.setParent(parentQuestion);

        nextQuestion.setLevel(parentQuestion.getLevel() + 1);

        Question savedQuestion = questionService.saveQuestion(nextQuestion);

        answer.setNextQuestion(savedQuestion);
        answerService.saveAnswer(answer);

        return "redirect:/questions/" + savedQuestion.getqId();
    }

    @GetMapping("/{id}/edit")
    public String editQuestionForm(@PathVariable Long id, Model model) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            model.addAttribute("question", questionOpt.get());
            return "editQuestion";
        }
        return "redirect:/forms";
    }

    @PostMapping("/{id}/edit")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute Question updatedQuestion,
            RedirectAttributes redirectAttributes) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            question.setText(updatedQuestion.getText());
            questionService.saveQuestion(question);
            redirectAttributes.addFlashAttribute("message", "Question updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Question not found");
        }
        return "redirect:/questions/" + id;
    }

    @GetMapping("/answers/{answerId}/edit")
    public String editAnswerForm(@PathVariable Long answerId, Model model) {
        Optional<Answer> answerOpt = answerService.getAnswerById(answerId);
        if (answerOpt.isPresent()) {
            Answer answer = answerOpt.get();
            model.addAttribute("answer", answer);
            model.addAttribute("questionType", answer.getQuestion().getType());
            return "editAnswer";
        }
        return "redirect:/forms";
    }

    @PostMapping("/answers/{answerId}/edit")
    public String updateAnswer(@PathVariable Long answerId,
            @ModelAttribute Answer updatedAnswer,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer maxNum,
            @RequestParam(required = false) Integer minNum) throws ParseException {
        Optional<Answer> answerOpt = answerService.getAnswerById(answerId);
        if (answerOpt.isPresent()) {
            Answer answer = answerOpt.get();
            String questionType = answer.getQuestion().getType();

            switch (questionType) {
                case "choice":
                    ((ChoiceAnswer) answer).setText(text);
                    break;
                case "date":
                    if (date != null && !date.isEmpty()) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        ((DateAnswer) answer).setDate(format.parse(date));
                    }
                    break;
                case "time":
                    if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        ((TimeSlot) answer).setStartTime(timeFormat.parse(startTime));
                        ((TimeSlot) answer).setEndTime(timeFormat.parse(endTime));
                    }
                    break;
                case "number":
                    ((NumberAnswer) answer).setMaxNum(maxNum);
                    ((NumberAnswer) answer).setMinNum(minNum);
                    break;
            }

            answer.setQty(updatedAnswer.getQty());
            if (answer.getQty() != null && answer.getQty() > 0) {
                answer.setRemainingQty(answer.getQty());
            } else {
                answer.setQty(null);
                answer.setRemainingQty(null);
            }

            answerService.saveAnswer(answer);
            return "redirect:/questions/" + answer.getQuestion().getqId();
        }
        return "redirect:/forms";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Question> questionOpt = questionService.getQuestionById(id);
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                Long parentId = question.getParent() != null ? question.getParent().getqId() : null;
                questionService.deleteQuestion(id);

                redirectAttributes.addFlashAttribute("message", "Question deleted successfully");

                if (parentId != null) {
                    return "redirect:/questions/" + parentId;
                } else {
                    return "redirect:/forms";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Question not found");
                return "redirect:/forms";
            }
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forms";
        }
    }

    @PostMapping("/answers/{answerId}/delete")
    public String deleteAnswer(@PathVariable Long answerId, RedirectAttributes redirectAttributes) {
        try {
            Answer answer = answerService.getAnswerById(answerId)
                    .orElseThrow(() -> new RuntimeException("Answer not found"));
            Long questionId = answer.getQuestion().getqId();

            answerService.deleteAnswer(answerId);
            redirectAttributes.addFlashAttribute("message", "Answer deleted successfully");
            return "redirect:/questions/" + questionId;
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forms";
        }
    }

}
