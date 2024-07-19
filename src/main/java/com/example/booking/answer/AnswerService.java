package com.example.booking.answer;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.choice_answer.ChoiceAnswer;
import com.example.booking.choice_answer.ChoiceAnswerRepository;
import com.example.booking.question.Question;
import com.example.booking.question.QuestionService;

import jakarta.transaction.Transactional;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionService questionService;
    @Autowired
    private ChoiceAnswerRepository choiceAnswerRepository;

    public Optional<Answer> getAnswerById(Long answerId) {
        return answerRepository.findById(answerId);
    }
    public List<Answer> getAnswersByQuestion(Long questionId) {
        return answerRepository.findByQuestion_qId(questionId);
    }
    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public ChoiceAnswer saveAnswer(ChoiceAnswer answer) {
        return choiceAnswerRepository.save(answer);
    }

    public void addNextQuestionToAnswer(Long answerId, Question nextQuestion) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        Question savedNextQuestion = questionService.saveQuestion(nextQuestion);
        answer.setNextQuestion(savedNextQuestion);
        answerRepository.save(answer);
    }
    @Transactional
    public Answer createAnswer(Answer answer) {
        answer.initializeRemainingQty();
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer updateAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer getAnswerByIdWithLock(Long answerId) {
        Answer answer = answerRepository.findByIdWithLock(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        if (answer.getRemainingQty() == null && answer.getQty() != null) {
            answer.initializeRemainingQty();
        }
        return answer;
    }

}
