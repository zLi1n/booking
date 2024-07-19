package com.example.booking.detail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.booking.booking.Booking;
import com.example.booking.question.Question;
import com.example.booking.user_select.UserSelect;


public interface DetailRepository extends JpaRepository<Detail, Long> {
    List<Detail> findByQuestion(Question question);
    List<Detail> findByBookingBId(Long bookingId);
    List<Detail> findByBooking(Booking booking);

    @Query("SELECT us FROM UserSelect us WHERE us.booking.bId = :bookingId AND us.answer.aId = :answerId")
    List<UserSelect> findUserSelectByBookingAndAnswer(@Param("bookingId") Long bookingId, @Param("answerId") Long answerId);
}

