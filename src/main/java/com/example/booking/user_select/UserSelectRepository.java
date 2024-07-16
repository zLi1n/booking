package com.example.booking.user_select;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.booking.answer.Answer;
import com.example.booking.booking.Booking;
import com.example.booking.question.Question;

public interface UserSelectRepository extends JpaRepository<UserSelect, Long> {
    @Query("SELECT u FROM UserSelect u WHERE u.question = :question AND u.booking = :booking")
    List<UserSelect> findByDetail(@Param("question") Question question, @Param("booking") Booking booking);

    List<UserSelect> findByAnswer(Answer answer);
}
