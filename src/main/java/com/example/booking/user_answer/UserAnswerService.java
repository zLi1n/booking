package com.example.booking.user_answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserAnswerService {
    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Transactional
    public UserAnswer saveUserAnswer(UserAnswer userAnswer) {
        return userAnswerRepository.save(userAnswer);
    }
}
