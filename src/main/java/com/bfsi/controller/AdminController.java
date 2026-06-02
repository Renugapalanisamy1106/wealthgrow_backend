package com.bfsi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bfsi.dto.AddMutualFundDTO;
import com.bfsi.dto.AdminDashboardDTO;
import com.bfsi.dto.EvaluationReviewDTO;
import com.bfsi.entity.ScenarioNavSeries;
import com.bfsi.dto.ScenarioDTO;
import com.bfsi.dto.UpdateMutualFundDTO;
import com.bfsi.dto.UserProfileDTO;
import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.service.AdminBO;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminBO adminBO;

    public AdminController(AdminBO adminBO) {
        this.adminBO = adminBO;
    }

    /* ============================
       DASHBOARD
       ============================ */

    @GetMapping(value = "/dashboard", produces = "application/json")
    public AdminDashboardDTO getDashboard() {
        return adminBO.getDashboard();
    }

    /* ============================
       MUTUAL FUND MANAGEMENT
       ============================ */

    @PostMapping(value = "/funds", consumes = "application/json")
    public String addMutualFund(@RequestBody AddMutualFundDTO dto) {
        adminBO.addMutualFund(dto);
        return "Mutual fund added successfully";
    }

    @GetMapping(value = "/funds", produces = "application/json")
    public List<MutualFundProduct> getAllMutualFunds() {
        return adminBO.getAllMutualFunds();
    }

    @PutMapping(value = "/funds", consumes = "application/json")
    public String updateMutualFund(@RequestBody UpdateMutualFundDTO dto) {
        adminBO.updateMutualFund(dto);
        return "Mutual fund updated successfully";
    }

    @DeleteMapping("/funds/{fundId}")
    public String deleteMutualFund(@PathVariable String fundId) {
        adminBO.deleteMutualFund(fundId);
        return "Mutual fund deleted successfully";
    }

    @PutMapping("/funds/{fundId}/promote")
    public String promoteFund(@PathVariable String fundId) {
        adminBO.promoteFund(fundId);
        return "Mutual fund promoted successfully";
    }

    @PutMapping("/funds/{fundId}/demote")
    public String demoteFund(@PathVariable String fundId) {
        adminBO.demoteFund(fundId);
        return "Mutual fund demoted successfully";
    }

    /* ============================
       SCENARIO MANAGEMENT
       ============================ */

    @PostMapping(value = "/scenarios", consumes = "application/json")
    public String addScenario(@RequestBody ScenarioDTO dto) {
        adminBO.addScenario(dto);
        return "Scenario added successfully";
    }

    @GetMapping(value = "/scenarios", produces = "application/json")
    public List<ScenarioAnalysis> getAllScenarios() {
        return adminBO.getAllScenarios();
    }

    @PutMapping(value = "/scenarios", consumes = "application/json")
    public String updateScenario(@RequestBody ScenarioDTO dto) {
        adminBO.updateScenario(dto);
        return "Scenario updated successfully";
    }

    @DeleteMapping("/scenarios/{scenarioId}")
    public String deleteScenario(@PathVariable String scenarioId) {
        adminBO.deleteScenario(scenarioId);
        return "Scenario deleted successfully";
    }

    /* ============================
       EVALUATION APPROVAL
       ============================ */

    @GetMapping(value = "/evaluations/pending", produces = "application/json")
    public List<DataEvaluation> viewPendingEvaluations() {
        return adminBO.viewPendingEvaluations();
    }

    @PutMapping("/evaluations/{evaluationId}/approve")
    public String approveEvaluation(@PathVariable String evaluationId) {
        adminBO.approveEvaluation(evaluationId);
        return "Evaluation approved successfully";
    }

    @PutMapping("/evaluations/{evaluationId}/reject")
    public String rejectEvaluation(@PathVariable String evaluationId) {
        adminBO.rejectEvaluation(evaluationId);
        return "Evaluation rejected successfully";
    }

    /**
     * ✅ NEW ENDPOINT — loads full review data for Admin sreview page.
     * Returns: evaluation + scenario + BA's impact results + nav series for graph.
     */
    @GetMapping(value = "/evaluations/{evaluationId}/review", produces = "application/json")
    public EvaluationReviewDTO getEvaluationReview(@PathVariable String evaluationId) {
        return adminBO.getEvaluationReview(evaluationId);
    }

    // ✅ NEW — per-impact nav series for multi-line chart (Admin-specific endpoint)
    @GetMapping(value = "/nav-series/impact/{impactId}", produces = "application/json")
    public List<ScenarioNavSeries> getNavSeriesByImpact(@PathVariable String impactId) {
        return adminBO.getNavSeriesByImpact(impactId);
    }

    /* ============================
       ADMIN PROFILE
       ============================ */

    @GetMapping(value = "/profile/{adminId}", produces = "application/json")
    public UserProfileDTO viewProfile(@PathVariable String adminId) {
        return adminBO.viewProfile(adminId);
    }

    @PutMapping(value = "/profile", consumes = "application/json")
    public String updateProfile(@RequestBody UserProfileDTO dto) {
        adminBO.updateProfile(dto);
        return "Admin profile updated successfully";
    }
}
