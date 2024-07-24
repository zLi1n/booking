package com.example.booking.time_answer;

import com.example.booking.answer.Answer;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class TimeSlot extends Answer {
    
    @Temporal(TemporalType.TIME)
    private Date startTime;
    
    @Temporal(TemporalType.TIME)
    private Date endTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public String getFormattedTimeRange() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        return sdf.format(startTime) + " - " + sdf.format(endTime);
    }
}

