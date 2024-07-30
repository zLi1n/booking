package com.example.booking.form;

import com.example.booking.display.Display;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fId;

    private String title;

    private Long firstQuestionId;

    @OneToOne(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private Display display;

    public Long getfId() {
        return fId;
    }

    public void setfId(Long fId) {
        this.fId = fId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getFirstQuestionId() {
        return firstQuestionId;
    }

    public void setFirstQuestionId(Long firstQuestionId) {
        this.firstQuestionId = firstQuestionId;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

}
