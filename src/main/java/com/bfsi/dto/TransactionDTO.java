package com.bfsi.dto;

import java.time.LocalDate;

public class TransactionDTO {

    private String transactionId;
    private String transactionType;
    private double amount;
    private String status;
    private LocalDate transactionDate;

    public TransactionDTO() {}

    public TransactionDTO(String transactionId,
                          String transactionType,
                          double amount,
                          String status,
                          LocalDate transactionDate) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}