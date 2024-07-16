package com.example.booking.user_select;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.answer.Answer;
import com.example.booking.booking.Booking;
import com.example.booking.detail.Detail;
import com.example.booking.question.Question;

@Service
public class UserSelectService {
    @Autowired
    private UserSelectRepository userSelectRepository;

    public UserSelect saveUserSelect(UserSelect userSelect) {
        return userSelectRepository.save(userSelect);
    }

    public Optional<UserSelect> getUserSelectById(Long id) {
        return userSelectRepository.findById(id);
    }

    public List<UserSelect> getUserSelectsByDetail(Detail detail) {
        Question question = detail.getQuestion();
        Booking booking = detail.getBooking();
        return userSelectRepository.findByDetail(question, booking);
    }

    public List<UserSelect> getUserSelectsByAnswer(Answer answer) {
        return userSelectRepository.findByAnswer(answer);
    }

    public void deleteUserSelect(Long id) {
        userSelectRepository.deleteById(id);
    }
}
