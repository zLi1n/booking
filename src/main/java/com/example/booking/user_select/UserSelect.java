package com.example.booking.user_select;

import java.text.SimpleDateFormat;

import com.example.booking.answer.Answer;
import com.example.booking.choice_answer.ChoiceAnswer;
import com.example.booking.date_answer.DateAnswer;
import com.example.booking.detail.Detail;
import com.example.booking.number_answer.NumberAnswer;
import com.example.booking.time_answer.TimeSlot;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("UserSelect")
public class UserSelect extends Detail {
    @ManyToOne
    @JoinColumn(name = "aId", nullable = false)
    private Answer answer;

    private Integer qty;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

}
