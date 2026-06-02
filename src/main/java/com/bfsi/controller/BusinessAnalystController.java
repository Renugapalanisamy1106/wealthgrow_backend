package com.bfsi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.bfsi.dto.UserProfileDTO;
import com.bfsi.dto.FundPerformanceDTO;
import com.bfsi.dto.ScenarioImpactRequestDTO;
import com.bfsi.dto.SubmitScenarioRequestDTO;
import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;
import com.bfsi.service.BusinessAnalystBO;

/**
 * REST Controller for Business Analyst operations
 */
@RestController
@RequestMapping("/ba")
@CrossOrigin(origins = "http://localhost:4200")

public class BusinessAnalystController {

    private final BusinessAnalystBO analystBO;

    public BusinessAnalystController(BusinessAnalystBO analystBO) {
        this.analystBO = analystBO;
    }

    /* ============================
       VIEW ALL SCENARIOS
       ============================ */
    @GetMapping(value = "/scenarios", produces = "application/json")
    public List<ScenarioAnalysis> viewAllScenarios() {
        return analystBO.viewAllScenarios();
    }

    /* ============================
       VIEW FUNDS FOR EVALUATION
       ============================ */
    @GetMapping(value = "/funds", produces = "application/json")
    public List<MutualFundProduct> viewFundsForEvaluation() {
        return analystBO.getFundsForEvaluation();
    }

    /* ============================
       SAVE SCENARIO IMPACT (BA)
       ============================ */
    @PostMapping(value = "/scenario-impact", consumes = "application/json")
    public String saveScenarioImpact(
            @RequestBody ScenarioImpactRequestDTO request) {

        analystBO.saveScenarioImpactResult(
                request.getScenarioId(),
                request.getFundId(),
                request.getRiskImpact(),
                request.getStabilityScore(),
                request.getRecommendation(),
                request.getAnalysisData()
        );

        return "Scenario impact result saved successfully";
    }

    /* ============================
       SUBMIT SCENARIO FOR APPROVAL
       ============================ */
    @PostMapping(
            value = "/submit",
            consumes = "application/json",
            produces = "application/json"
    )
    public DataEvaluation submitForApproval(
            @RequestBody SubmitScenarioRequestDTO request) {

        return analystBO.submitScenarioAnalysisForApproval(
                request.getScenarioId(),
                request.getRoleToApprove(),
                request.getSubmittedBy()
        );
    }

    /* ============================
       VIEW APPROVED IMPACT RESULTS
       ============================ */
    @GetMapping(value = "/impact/{scenarioId}", produces = "application/json")
    public List<ScenarioImpactResult> viewApprovedImpactResults(
            @PathVariable String scenarioId) {

        return analystBO.viewScenarioImpactResults(scenarioId);
    }

    /* ============================
       ✅ CSV UPLOAD + ANALYTICS + DB SAVE (SINGLE FILE)
       ============================ */
    @PostMapping(
            value = "/upload-csv",
            consumes = "multipart/form-data",
            produces = "application/json"
    )
    public FundPerformanceDTO uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scenarioId") String scenarioId,
            @RequestParam("fundId") String fundId) {

        System.out.println("File received: " + file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        return analystBO.uploadCsv(file, scenarioId, fundId);
    }

    /* ============================
       ✅✅ NEW: BATCH CSV UPLOAD (MULTIPLE FILES)
       ============================ */
    @PostMapping(
            value = "/upload-scenario-batch",
            consumes = "multipart/form-data",
            produces = "application/json"
    )
    public String uploadScenarioBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam String scenarioId) {

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("No files provided");
        }

        analystBO.uploadCsvBatch(files, scenarioId);

        return "Batch upload successful";
    }

    /* ============================
       PROFILE (BUSINESS ANALYST)
       ============================ */

    @GetMapping(value = "/profile/{userId}", produces = "application/json")
    public UserProfileDTO viewProfile(@PathVariable String userId) {
        return analystBO.viewProfile(userId);
    }

    @PutMapping(value = "/profile", consumes = "application/json")
    public String updateProfile(@RequestBody UserProfileDTO dto) {
        analystBO.updateProfile(dto);
        return "Business Analyst profile updated successfully";
    }

    @GetMapping("/nav-series/{impactId}")
    public List<ScenarioNavSeries> getNavSeries(@PathVariable String impactId) {
        return analystBO.getNavSeries(impactId);
    }

    @GetMapping("/pending")
    public List<ScenarioImpactResult> getPendingApprovals() {
        return analystBO.getPendingApprovals();
    }
    @GetMapping("/evaluations")
    public List<DataEvaluation> getEvaluations(
            @RequestParam(required = false) String userId) {

        return analystBO.getAllEvaluations(userId);
    }
@GetMapping("/dashboard")
public Object getDashboard() {
    return analystBO.getDashboardStats();
}
@GetMapping("/notifications")
    public List<DataEvaluation> getNotifications(
            @RequestParam(required = false) String userId) {

        return analystBO.getNotifications(userId);
    }
}