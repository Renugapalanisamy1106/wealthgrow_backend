package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions", schema = "bfsimf_clean")
public class Transaction {

    @Id
    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "investor_id")
    private String investorId;

    @Column(name = "fund_id")
    private String fundId;

    @Column(name = "txn_type")
    private String txnType;

    @Column(name = "amount")
    private double amount;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "status")
    private String status;

    @Column(name = "txn_date")
    private LocalDate txnDate;

    // ✅ ✅ ✅ REMOVED units field (IMPORTANT FIX)

    public Transaction() {
    }

    public Transaction(String txnId,
                       String investorId,
                       String fundId,
                       String txnType,
                       double amount,
                       String paymentMode,
                       String status,
                       LocalDate txnDate) {

        this.txnId = txnId;
        this.investorId = investorId;
        this.fundId = fundId;
        this.txnType = txnType;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.status = status;
        this.txnDate = txnDate;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDate txnDate) {
        this.txnDate = txnDate;
    }
}