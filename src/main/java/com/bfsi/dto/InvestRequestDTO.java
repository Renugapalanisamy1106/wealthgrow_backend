package com.bfsi.dto;

/**
 * DTO used by Investor to invest in a Mutual Fund
 */
public class InvestRequestDTO {

    private String investorId;
    private String fundId;
    private double amount;

    // ✅ Default constructor (required for frameworks & tests)
    public InvestRequestDTO() {
    }

    // ✅ Parameterized constructor
    public InvestRequestDTO(String investorId,
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