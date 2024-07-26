package com.example.booking.answer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.booking.question.Question;

import jakarta.persistence.LockModeType;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);
    List<Answer> findByQuestion_qId(Long questionId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Answer a WHERE a.aId = :answerId")
    Optional<Answer> findByIdWithLock(@Param("answerId") Long answerId);
    List<Answer> findByNextQuestion(Question question);
    boolean existsByNextQuestionAndAIdNot(Question nextQuestion, Long aId);
}

