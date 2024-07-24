package com.example.booking.question;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

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
    
}
