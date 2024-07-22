package com.example.booking.date_answer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.booking.answer.Answer;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class DateAnswer extends Answer {
    @Temporal(TemporalType.DATE)
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
