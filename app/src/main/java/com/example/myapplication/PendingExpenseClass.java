package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class PendingExpenseClass implements Serializable {
    private UUID id;
    private double amount;
    private Date date;

    // Constructor
    public PendingExpenseClass(double amount, Date date) {
        this.id = UUID.randomUUID(); // Generate a random UUID for the ID
        this.amount = amount;
        this.date = date;
    }

    public PendingExpenseClass(UUID id, double amount, Date date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // toString method to represent PendingExpense object as a string
    @Override
    public String toString() {
        return "PendingExpense{" +
                "id=" + id +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}