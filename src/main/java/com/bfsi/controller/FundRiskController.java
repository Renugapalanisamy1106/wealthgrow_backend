package com.bfsi.controller;

import com.bfsi.entity.FundRiskAnalysis;
import com.bfsi.service.FundRiskBO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = {
    "http://localhost:4200",
    "https://wealthgrow-frontend.vercel.app"
})
public class FundRiskController {

    private final FundRiskBO fundRiskBO;

    public FundRiskController(FundRiskBO fundRiskBO) {
        this.fundRiskBO = fundRiskBO;
    }

    /* ============================
       BA — Upload CSV + Analyse
       POST /ba/fund-risk/analyse
       ============================ */
    @PostMapping(value = "/ba/fund-risk/analyse",
                 consumes = "multipart/form-data",
                 produces = "application/json")
    public FundRiskAnalysis analyseFundRisk(
            @RequestParam("file")        MultipartFile file,
            @RequestParam("fundId")      String fundId,
            @RequestParam("submittedBy") String submittedBy) {

        return fundRiskBO.analyseAndSave(file, fundId, submittedBy);
    }

    /* ============================
       BA — Get own submissions
       GET /ba/fund-risk/my?userId=
       ============================ */
    @GetMapping("/ba/fund-risk/my")
    public List<FundRiskAnalysis> getMyAnalyses(
            @RequestParam(required = false) String userId) {

        if (userId == null || userId.isBlank()) {
            return fundRiskBO.getAllAnalyses();
        }
        return fundRiskBO.getBySubmittedBy(userId);
    }

    /* ============================
       ADMIN — Get pending analyses
       GET /admin/fund-risk/pending
       ============================ */
    @GetMapping("/admin/fund-risk/pending")
    public List<FundRiskAnalysis> getPendingAnalyses() {
        return fundRiskBO.getPendingAnalyses();
    }

    /* ============================
       ADMIN — Get all analyses
       GET /admin/fund-risk/all
       ============================ */
    @GetMapping("/admin/fund-risk/all")
    public List<FundRiskAnalysis> getAllAnalyses() {
        return fundRiskBO.getAllAnalyses();
    }

    /* ============================
       ADMIN — Approve → updates fund risk
       PUT /admin/fund-risk/{id}/approve
       ============================ */
    @PutMapping("/admin/fund-risk/{analysisId}/approve")
    public String approveAnalysis(@PathVariable String analysisId) {
        fundRiskBO.approve(analysisId);
        return "Fund risk analysis approved and fund risk level updated successfully";
    }

    /* ============================
       ADMIN — Reject
       PUT /admin/fund-risk/{id}/reject
       ============================ */
    @PutMapping("/admin/fund-risk/{analysisId}/reject")
    public String rejectAnalysis(@PathVariable String analysisId) {
        fundRiskBO.reject(analysisId);
        return "Fund risk analysis rejected";
    }
}
