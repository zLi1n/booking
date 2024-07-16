package com.example.booking.detail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.booking.booking.Booking;
import com.example.booking.user_answer.UserAnswer;
import com.example.booking.user_select.UserSelect;

@Service
public class DetailService {
    @Autowired
    private DetailRepository detailRepository;

    public void saveDetail(Detail detail) {
        detailRepository.save(detail);
    }

    public Detail createDetail(DetailType type) {
        switch (type) {
            case USER_ANSWER:
                return new UserAnswer();
            case USER_SELECT:
                return new UserSelect();
            default:
                throw new IllegalArgumentException("Unknown detail type");
        }
    }

    public enum DetailType {
        USER_ANSWER,
        USER_SELECT
    }

    public List<Detail> getDetailsByBooking(Booking booking) {
        return detailRepository.findByBooking(booking);
    }
}
