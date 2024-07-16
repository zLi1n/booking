package com.example.booking.detail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.booking.Booking;
import com.example.booking.question.Question;


public interface DetailRepository extends JpaRepository<Detail, Long> {
    List<Detail> findByQuestion(Question question);
    List<Detail> findByBookingBId(Booking booking);
    List<Detail> findByBooking(Booking booking);
}

