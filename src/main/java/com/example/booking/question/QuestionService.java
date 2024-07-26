package com.example.booking.question;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.booking.answer.Answer;
import com.example.booking.answer.AnswerRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    public Question getFirstLevelQuestion() {
        return questionRepository.findByParentIsNull();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> getChildQuestions(Long parentId) {
        return questionRepository.findByParentQId(parentId);
    }

    public Optional<Question> getParentQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .map(Question::getParent);
    }

    public Question getQuestionWithAnswers(Long id) {
        Question question = getQuestionById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        Hibernate.initialize(question.getAnswers());
        return question;
    }

    @Transactional
    public void deleteQuestion(Long id) throws DataIntegrityViolationException {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        try {
            List<Answer> parentAnswers = answerRepository.findByNextQuestion(question);
            for (Answer parentAnswer : parentAnswers) {
                parentAnswer.setNextQuestion(null);
                answerRepository.save(parentAnswer);
            }

            for (Question childQuestion : question.getChildren()) {
                deleteQuestion(childQuestion.getqId());
            }

            questionRepository.delete(question);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("This question cannot be deleted as it has been booked.");
        }
    }
}
