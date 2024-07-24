package com.example.booking.number_answer;

import com.example.booking.answer.Answer;

import jakarta.persistence.Entity;

@Entity
public class NumberAnswer extends Answer {
    private int maxNum;
    private int minNum;
    
    public int getMaxNum() {
        return maxNum;
    }
    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }
    public int getMinNum() {
        return minNum;
    }
    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }
    
}
