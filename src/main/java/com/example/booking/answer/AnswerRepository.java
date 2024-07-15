package com.example.booking.answer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.question.Question;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestion(Question question);
}

