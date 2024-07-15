package com.example.booking.user_answer;

import com.example.booking.detail.Detail;

import jakarta.persistence.Entity;

@Entity
public class UserAnswer extends Detail {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}