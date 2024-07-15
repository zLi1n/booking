package com.example.booking.choice_answer;

import com.example.booking.answer.Answer;

import jakarta.persistence.Entity;

@Entity
public class ChoiceAnswer extends Answer {
    private String text;
    private Integer qty;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

}
