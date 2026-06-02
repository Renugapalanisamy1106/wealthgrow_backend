package com.bfsi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bfsi.dto.UserProfileDTO;
import com.bfsi.dto.InvestorDashboardDTO;
import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;   // ✅ NEW
import com.bfsi.service.PortfolioManagerBO;

/**
 * REST Controller for Portfolio Manager operations
 */
@RestController
@RequestMapping("/portfolio-manager")
public class PortfolioManagerController {

    private final PortfolioManagerBO portfolioManagerBO;

    public PortfolioManagerController(PortfolioManagerBO portfolioManagerBO) {
        this.portfolioManagerBO = portfolioManagerBO;
    }

    /* ============================
       DASHBOARD METRICS
       ============================ */

    @GetMapping(value = "/dashboard/total-funds", produces = "application/json")
    public int getTotalFunds() {
        return portfolioManagerBO.getTotalFunds();
    }

    @GetMapping(value = "/dashboard/active-users", produces = "application/json")
    public int getActiveUsers() {
        return portfolioManagerBO.getActiveUsers();
    }

    @GetMapping(value = "/dashboard/pending-evaluations", produces = "application/json")
    public int getPendingEvaluations() {
        return portfolioManagerBO.getPendingRequestsCount();
    }

    /* ============================
       INVESTOR MONITORING
       ============================ */

    @GetMapping(value = "/investors", produces = "application/json")
    public List<UserProfileDTO> getAllInvestors() {
        return portfolioManagerBO.getAllInvestors();
    }

    @GetMapping(value = "/investor-dashboard/{investorId}", produces = "application/json")
    public InvestorDashboardDTO getInvestorDashboard(@PathVariable String investorId) {
        return portfolioManagerBO.getInvestorDashboard(investorId);
    }

    /* ============================
       MUTUAL FUNDS (READ ONLY)
       ============================ */

    @GetMapping(value = "/funds", produces = "application/json")
    public List<MutualFundProduct> viewAllFunds() {
        return portfolioManagerBO.viewAllFunds();
    }

    /* ============================
       SCENARIOS (READ ONLY)
       ============================ */

    @GetMapping(value = "/scenarios", produces = "application/json")
    public List<ScenarioAnalysis> viewScenarios() {
        return portfolioManagerBO.viewScenarios();
    }

    // ✅ NEW — get single scenario by ID (for scenario review page header)
    @GetMapping(value = "/scenarios/{scenarioId}", produces = "application/json")
    public ScenarioAnalysis getScenarioById(@PathVariable String scenarioId) {
        return portfolioManagerBO.getScenarioById(scenarioId);
    }

    /* ============================
       SCENARIO IMPACT APPROVAL
       ============================ */

    @GetMapping(value = "/scenario-impact/{scenarioId}", produces = "application/json")
    public List<ScenarioImpactResult> viewScenarioImpactForApproval(
            @PathVariable String scenarioId) {
        return portfolioManagerBO.viewScenarioImpactForApproval(scenarioId);
    }

    @PutMapping("/scenario-impact/{impactId}/approve")
    public String approveScenarioImpact(@PathVariable String impactId) {
        portfolioManagerBO.approveScenarioImpact(impactId);
        return "Scenario impact approved successfully";
    }

    @PutMapping("/scenario-impact/{impactId}/reject")
    public String rejectScenarioImpact(@PathVariable String impactId) {
        portfolioManagerBO.rejectScenarioImpact(impactId);
        return "Scenario impact rejected successfully";
    }

    // NAV time-series by scenarioId (flat, all impacts combined)
    @GetMapping(value = "/nav-series/{scenarioId}", produces = "application/json")
    public List<ScenarioNavSeries> getNavSeriesByScenario(@PathVariable String scenarioId) {
        return portfolioManagerBO.getNavSeriesByScenario(scenarioId);
    }

    // ✅ NEW — NAV time-series by impactId (per-fund, for multi-line chart)
    @GetMapping(value = "/nav-series/impact/{impactId}", produces = "application/json")
    public List<ScenarioNavSeries> getNavSeriesByImpact(@PathVariable String impactId) {
        return portfolioManagerBO.getNavSeriesByImpact(impactId);
    }

    /* ============================
       EVALUATION WORKFLOW
       ============================ */

    @GetMapping(value = "/evaluations/pending", produces = "application/json")
    public List<DataEvaluation> viewPendingEvaluations() {
        return portfolioManagerBO.getPendingEvaluationsList();
    }

    @PutMapping("/evaluations/{evaluationId}/approve")
    public String approveEvaluation(@PathVariable String evaluationId) {
        portfolioManagerBO.approveEvaluation(evaluationId);
        return "Evaluation approved successfully";
    }

    @PutMapping("/evaluations/{evaluationId}/reject")
    public String rejectEvaluation(@PathVariable String evaluationId) {
        portfolioManagerBO.rejectEvaluation(evaluationId);
        return "Evaluation rejected successfully";
    }

    /* ============================
       PROFILE MANAGEMENT
       ============================ */

    @GetMapping(value = "/profile/{pmId}", produces = "application/json")
    public UserProfileDTO viewProfile(@PathVariable String pmId) {
        return portfolioManagerBO.viewProfile(pmId);
    }

    @PutMapping(value = "/profile", consumes = "application/json")
    public String updateProfile(@RequestBody UserProfileDTO dto) {
        portfolioManagerBO.updateProfile(dto);
        return "Profile updated successfully";
    }
}
