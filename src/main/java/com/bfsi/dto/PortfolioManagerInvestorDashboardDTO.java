package com.bfsi.dto;

import java.time.LocalDate;

public class PortfolioManagerInvestorDashboardDTO {

    private String investorName;
    private String fundName;
    private String action;
    private LocalDate date;
    private String status;

    public PortfolioManagerInvestorDashboardDTO(String investorName,
                                                String fundName,
                                                String action,
                                                LocalDate date,
                                                String status) {
        this.investorName = investorName;
        this.fundName = fundName;
        this.action = action;
        this.date = date;
        this.status = status;
    }

    public String getInvestorName() {
        return investorName;
    }

    public String getFundName() {
        return fundName;
    }

    public String getAction() {
        return action;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}