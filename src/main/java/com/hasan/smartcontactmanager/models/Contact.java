package com.hasan.smartcontactmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    @NotBlank(message = "Name field is required !!")
    private String name;

    private String secondName;
    private String work;
    @NotBlank(message = "Phone number required !!")
    private String phone;
    @NotBlank(message = "Email can't be empty!")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "Invalid email !!")
    private String email;

    private String imageUrl;
    @Column(length = 50000)
    private String description;

    @ManyToOne
    @JsonIgnore
    private User user;

    public Contact() {
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
