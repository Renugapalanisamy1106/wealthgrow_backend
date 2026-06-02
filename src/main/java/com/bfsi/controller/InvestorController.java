package com.bfsi.controller;


import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.bfsi.dto.InvestRequestDTO;
import com.bfsi.dto.InvestorDashboardDTO;
import com.bfsi.dto.RaiseComplaintDTO;
import com.bfsi.dto.WithdrawRequestDTO;
import com.bfsi.dto.UserProfileDTO;
import com.bfsi.entity.InvestorProfile;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.Portfolio;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;
import com.bfsi.entity.Transaction;
import com.bfsi.service.InvestorBO;

@CrossOrigin(origins = "http://localhost:4200")
/**
 * REST Controller for Investor operations
 */
@RestController
@RequestMapping("/investor")
public class InvestorController {

    private final InvestorBO investorBO;

    public InvestorController(InvestorBO investorBO) {
        this.investorBO = investorBO;
    }

    /* ============================
       MUTUAL FUNDS
       ============================ */

    @GetMapping(value = "/funds", produces = "application/json")
    public List<MutualFundProduct> getAvailableFunds() {
        return investorBO.getAvailableFunds();
    }

    /* ============================
       PORTFOLIO
       ============================ */

    @GetMapping(value = "/portfolio/{investorId}", produces = "application/json")
    public List<Portfolio> getPortfolio(
            @PathVariable String investorId) {
        return investorBO.getInvestorPortfolio(investorId);
    }

    @GetMapping(value = "/total/{investorId}", produces = "application/json")
    public double getTotalInvestment(
            @PathVariable String investorId) {
        return investorBO.getTotalInvestmentValue(investorId);
    }

    /* ============================
       TRANSACTIONS
       ============================ */

    @GetMapping(value = "/transactions/{investorId}", produces = "application/json")
    public List<Transaction> getTransactions(
            @PathVariable String investorId) {
        return investorBO.getTransactionHistory(investorId);
    }

    /* ============================
       PROFILE
       ============================ */

    @GetMapping(value = "/profile/{investorId}", produces = "application/json")
    public InvestorProfile getProfile(
            @PathVariable String investorId) {
        return investorBO.viewProfile(investorId);
    }

    /* ============================
       INVEST / WITHDRAW
       ============================ */

    @PostMapping(value = "/invest", consumes = "application/json")
    public String invest(@RequestBody InvestRequestDTO dto) {
        investorBO.investInFund(dto);
        return "Investment successful";
    }

    @PostMapping(value = "/withdraw", consumes = "application/json")
    public String withdraw(@RequestBody WithdrawRequestDTO dto) {
        investorBO.withdrawFromFund(dto);
        return "Withdrawal successful";
    }

    /* ============================
       SCENARIO ANALYSIS
       ============================ */

    @GetMapping(value = "/scenario/{scenarioId}", produces = "application/json")
    public List<ScenarioImpactResult> getScenarioAnalysis(
            @PathVariable String scenarioId) {
        return investorBO.viewScenarioAnalysis(scenarioId);
    }

    // ✅ NEW — per-impact nav series for investor scenario analysis chart (JWT-safe)
    @GetMapping(value = "/nav-series/impact/{impactId}", produces = "application/json")
    public List<ScenarioNavSeries> getNavSeriesByImpact(@PathVariable String impactId) {
        return investorBO.getNavSeriesByImpact(impactId);
    }

    /* ============================
       COMPLAINTS
       ============================ */

    @PostMapping(value = "/complaint", consumes = "application/json")
    public String raiseComplaint(@RequestBody RaiseComplaintDTO dto) {
        investorBO.raiseComplaint(dto);
        return "Complaint submitted successfully";
    }

    /* ============================
       DASHBOARD
       ============================ */

    @GetMapping(value = "/dashboard/{investorId}", produces = "application/json")
    public InvestorDashboardDTO getDashboard(
            @PathVariable String investorId) {
        return investorBO.getDashboard(investorId);
    }

    @PutMapping(value = "/profile", consumes = "application/json")
public String updateProfile(@RequestBody UserProfileDTO dto) {
    investorBO.updateProfile(dto);
    return "Investor profile updated successfully";
}
}