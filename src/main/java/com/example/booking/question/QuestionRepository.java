package com.example.booking.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByParentQId(Long parentId);
    Question findByParentIsNull();
}

