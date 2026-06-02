package com.bfsi.dto;

public class DashboardMetricsDTO {

    private double totalInvestmentValue;
    private int totalFunds;
    private int totalTransactions;

    public DashboardMetricsDTO() {}

    public DashboardMetricsDTO(double totalInvestmentValue,
                               int totalFunds,
                               int totalTransactions) {
        this.totalInvestmentValue = totalInvestmentValue;
        this.totalFunds = totalFunds;
        this.totalTransactions = totalTransactions;
    }

    public double getTotalInvestmentValue() {
        return totalInvestmentValue;
    }

    public void setTotalInvestmentValue(double totalInvestmentValue) {
        this.totalInvestmentValue = totalInvestmentValue;
    }

    public int getTotalFunds() {
        return totalFunds;
    }

    public void setTotalFunds(int totalFunds) {
        this.totalFunds = totalFunds;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
}