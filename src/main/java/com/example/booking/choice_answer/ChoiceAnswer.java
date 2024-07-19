package com.example.booking.choice_answer;

import com.example.booking.answer.Answer;

import jakarta.persistence.Entity;

@Entity
public class ChoiceAnswer extends Answer {
    private String text;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
