package com.example.myapplication;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Expense implements Serializable {
    private  UUID id;
    private double amount;
    private String category;//need or want in capital
    private String description;
    private Date date;

    // Constructor
    public Expense(double amount, String category, String description, Date date) {
        this.id = UUID.randomUUID(); // Generate a random UUID for the ID
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public Expense(UUID id , double amount, String category, String description, Date date) {
        this.id = id; // Generate a random UUID for the ID
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID  id) {
        this.id =id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // toString method to represent Expense object as a string
    @NonNull
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}

