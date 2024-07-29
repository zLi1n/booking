package com.example.booking.user_select;

import com.example.booking.answer.Answer;
import com.example.booking.detail.Detail;
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
