package com.bfsi.dto;

public class WithdrawRequestDTO {

    private String investorId;
    private String fundId;
    private double amount;

    // ✅ Required by Spring/Jackson
    public WithdrawRequestDTO() {
    }

    // ✅ Convenience constructor
    public WithdrawRequestDTO(String investorId,
                              String fundId,
                              double amount) {
        this.investorId = investorId;
        this.fundId = fundId;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}