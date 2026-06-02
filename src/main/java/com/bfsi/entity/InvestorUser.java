package com.bfsi.entity;

import java.time.LocalDate;

public class InvestorUser {

    private String investorId;
    private String mobileNo;
    private String panNo;
    private String cardHolderName;
    private String creditCardNo;
    private LocalDate expiryDate;
    private String cvv;

    // ✅ Default constructor
    public InvestorUser() {
    }

    // ✅ Parameterized constructor
    public InvestorUser(String investorId, String mobileNo, String panNo,
                        String cardHolderName, String creditCardNo,
                        LocalDate expiryDate, String cvv) {
        this.investorId = investorId;
        this.mobileNo = mobileNo;
        this.panNo = panNo;
        this.cardHolderName = cardHolderName;
        this.creditCardNo = creditCardNo;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    // ✅ Getters and Setters
    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "InvestorUser{" +
                "investorId='" + investorId + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", panNo='" + panNo + '\'' +
                '}';
    }
}