package com.bfsi.dto;
import java.util.List;
import com.bfsi.entity.Transaction;

public class InvestorDashboardDTO {

    private double totalValue;
    private int activeFunds;
    private List<Transaction> recentTransactions;

    // ✅ Default constructor (mandatory for BO usage)
    public InvestorDashboardDTO() {
    }

    // ✅ Parameterized constructor (optional but useful)
    public InvestorDashboardDTO(double totalValue,
                                int activeFunds,
                                List<Transaction> recentTransactions) {
        this.totalValue = totalValue;
        this.activeFunds = activeFunds;
        this.recentTransactions = recentTransactions;
    }

    // ✅ Getters
    public double getTotalValue() {
        return totalValue;
    }

    public int getActiveFunds() {
        return activeFunds;
    }

    public List<Transaction> getRecentTransactions() {
        return recentTransactions;
    }

    // ✅ Setters (required for BO logic)
    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public void setActiveFunds(int activeFunds) {
        this.activeFunds = activeFunds;
    }

    public void setRecentTransactions(List<Transaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}