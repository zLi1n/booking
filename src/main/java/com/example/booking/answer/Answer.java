package com.example.booking.answer;

import com.example.booking.question.Question;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qId", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "next_qId")
    private Question nextQuestion;

    private Integer qty;
    private Integer remainingQty;

    public Long getaId() {
        return aId;
    }

    public void setaId(Long aId) {
        this.aId = aId;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(Integer remainingQty) {
        this.remainingQty = remainingQty;
    }
    public void initializeRemainingQty() {
        if (this.qty != null) {
            this.remainingQty = this.qty;
        }
    }

    public void updateRemainingQty(int requestedQuantity) {
        if (this.remainingQty != null) {
            this.remainingQty -= requestedQuantity;
        }
    }
}
