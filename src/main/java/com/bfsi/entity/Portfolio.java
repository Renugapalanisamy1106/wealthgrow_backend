package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * JPA Entity for investor_portfolio table
 */
@Entity
@Table(name = "investor_portfolio", schema = "bfsimf_clean")
public class Portfolio {

    @Id
    @Column(name = "portfolio_id")
    private String portfolioId;

    @Column(name = "investor_id")
    private String investorId;

    @Column(name = "fund_id")
    private String fundId;

    @Column(name = "unit_balance")
    private int unitBalance;

    @Column(name = "current_value")
    private double currentValue;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    public Portfolio() {
    }

    public Portfolio(String portfolioId,
                     String investorId,
                     String fundId,
                     int unitBalance,
                     double currentValue,
                     LocalDate purchaseDate) {

        this.portfolioId = portfolioId;
        this.investorId = investorId;
        this.fundId = fundId;
        this.unitBalance = unitBalance;
        this.currentValue = currentValue;
        this.purchaseDate = purchaseDate;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
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

    public int getUnitBalance() {
        return unitBalance;
    }

    public void setUnitBalance(int unitBalance) {
        this.unitBalance = unitBalance;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}