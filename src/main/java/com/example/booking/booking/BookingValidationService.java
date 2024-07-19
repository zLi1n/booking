package com.example.booking.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.answer.Answer;
import com.example.booking.answer.AnswerService;
import com.example.booking.detail.DetailService;

import jakarta.transaction.Transactional;

@Service
public class BookingValidationService {
    @Autowired
    private DetailService detailService;
    @Autowired
    private AnswerService answerService;

    public boolean isAvailableAnswer(Long bookingId, Answer answer) {
        return checkAvailabilityRecursive(bookingId, answer, 1);
    }

    private boolean checkAvailabilityRecursive(Long bookingId, Answer answer, int requestedQuantity) {
        Answer lockedAnswer = answerService.getAnswerByIdWithLock(answer.getaId());
        if (lockedAnswer.getQty() != null) {
            if (lockedAnswer.getRemainingQty() == null) {
                lockedAnswer.initializeRemainingQty();
                answerService.updateAnswer(lockedAnswer);
            }
            if (lockedAnswer.getRemainingQty() < requestedQuantity) {
                return false; 
            }
        }
        
        if (lockedAnswer.getNextQuestion() != null) {
            List<Answer> childAnswers = answerService.getAnswersByQuestion(lockedAnswer.getNextQuestion().getqId());
            return childAnswers.stream()
                    .anyMatch(childAnswer -> checkAvailabilityRecursive(bookingId, childAnswer, requestedQuantity));
        }
        
        return true;
    }

    @Transactional
    public boolean validateAndUpdateQuantity(Long bookingId, Long answerId, Integer requestedQuantity) {
        Answer answer = answerService.getAnswerByIdWithLock(answerId);
        if (!checkAvailabilityRecursive(bookingId, answer, requestedQuantity)) {
            return false;
        }
        
        updateQuantitiesRecursively(answer, requestedQuantity);
        return true;
    }

    private void updateQuantitiesRecursively(Answer answer, int requestedQuantity) {
        if (answer.getQty() != null) {
            if (answer.getRemainingQty() == null) {
                answer.initializeRemainingQty();
            }
            answer.updateRemainingQty(requestedQuantity);
            if (answer.getRemainingQty() < 0) {
                throw new RuntimeException("Insufficient quantity available");
            }
            answerService.updateAnswer(answer);
        }
        
        if (answer.getQty() == null) {
            return;
        }
        
        if (answer.getNextQuestion() != null) {
            List<Answer> childAnswers = answerService.getAnswersByQuestion(answer.getNextQuestion().getqId());
            for (Answer childAnswer : childAnswers) {
                updateQuantitiesRecursively(childAnswer, requestedQuantity);
            }
        }
    }
}