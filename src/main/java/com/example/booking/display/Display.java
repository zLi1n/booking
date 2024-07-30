package com.example.booking.display;

import com.example.booking.form.Form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;

@Entity
public class Display {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long displayId;

    @OneToOne
    @JoinColumn(name = "fId", nullable = false)
    private Form form;

    private String title;

    @Column(length = 1000)
    @Size(max = 1000, message = "Description must be no more than 1000 characters")
    private String description;
    
    @Column(length = 1000)
    private String bannerImagePath;

    public Long getDisplayId() {
        return displayId;
    }
    public void setDisplayId(Long displayId) {
        this.displayId = displayId;
    }
    public Form getForm() {
        return form;
    }
    public void setForm(Form form) {
        this.form = form;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getBannerImagePath() {
        return bannerImagePath;
    }
    public void setBannerImagePath(String bannerImagePath) {
        this.bannerImagePath = bannerImagePath;
    }

}
