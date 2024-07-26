package com.example.booking.answer;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.booking.choice_answer.ChoiceAnswer;
import com.example.booking.choice_answer.ChoiceAnswerRepository;
import com.example.booking.question.Question;
import com.example.booking.question.QuestionRepository;
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
    @Autowired
    private QuestionRepository questionRepository;

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

    @Transactional
    public void deleteAnswer(Long aId) throws DataIntegrityViolationException {
        Answer answer = answerRepository.findById(aId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        try {
            Question question = answer.getQuestion();
            question.getAnswers().remove(answer);

            Question nextQuestion = answer.getNextQuestion();
            if (nextQuestion != null) {
                boolean isReferenced = answerRepository.existsByNextQuestionAndAIdNot(nextQuestion, aId);
                if (!isReferenced) {
                    questionRepository.delete(nextQuestion);
                }
            }

            answerRepository.delete(answer);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("This answer cannot be deleted as it has been booked.");
        }
    }
}
