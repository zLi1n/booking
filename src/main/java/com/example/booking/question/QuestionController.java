package com.example.booking.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.booking.answer.Answer;
import com.example.booking.answer.AnswerService;
import com.example.booking.choice_answer.ChoiceAnswer;

import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @GetMapping
    public String viewFirstQuestion(Model model) {
        Question firstQuestion = questionService.getFirstLevelQuestion();
        if (firstQuestion == null) {
            return "redirect:/questions/add";
        }
        return viewQuestion(firstQuestion.getqId(), model);
    }

    @GetMapping("/add")
    public String addQuestionForm(Model model) {
        model.addAttribute("question", new Question());
        return "addQuestion";
    }

    @PostMapping("/add")
    public String addQuestion(@ModelAttribute Question question) {
        if (question.getParent() == null) {
            question.setLevel(1);
        }
        questionService.saveQuestion(question);
        return "redirect:/questions";
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            model.addAttribute("question", question);
            Optional<Question> parentQuestionOpt = questionService.getParentQuestion(id);
            parentQuestionOpt.ifPresent(parent -> model.addAttribute("parentQuestion", parent));

            // Create the appropriate type of answer based on the question type
            if ("choice".equals(question.getType())) {
                model.addAttribute("newAnswer", new ChoiceAnswer());
            } else {
                model.addAttribute("newAnswer", new Answer());
            }

            return "viewQuestion";
        } else {
            return "redirect:/questions";
        }
    }

    @PostMapping("/{id}/answers")
    public String addAnswer(@PathVariable Long id, @ModelAttribute ChoiceAnswer choiceAnswer) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            choiceAnswer.setQuestion(question);

            if ("choice".equals(question.getType())) {
                if (choiceAnswer.getQty() == null || choiceAnswer.getQty() == 0) {
                    choiceAnswer.setQty(null);
                    choiceAnswer.setRemainingQty(null);
                } else {
                    choiceAnswer.setRemainingQty(choiceAnswer.getQty());
                }
                answerService.createAnswer(choiceAnswer);
            } else {
                // Handle other question types
                Answer answer = new Answer();
                answer.setQuestion(question);
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

}
