package com.bfsi.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfsi.dto.AddMutualFundDTO;
import com.bfsi.dto.AdminDashboardDTO;
import com.bfsi.dto.EvaluationReviewDTO;
import com.bfsi.dto.ScenarioDTO;
import com.bfsi.dto.UpdateMutualFundDTO;
import com.bfsi.dto.UserProfileDTO;
import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.AdminRepository;
import com.bfsi.repository.DataEvaluationRepository;
import com.bfsi.repository.MutualFundRepository;
import com.bfsi.repository.ScenarioImpactResultRepository;
import com.bfsi.repository.ScenarioNavSeriesRepository;
import com.bfsi.repository.ScenarioRepository;
import com.bfsi.repository.UserProfileRepository;
import com.bfsi.repository.UserRepository;

@Service
public class AdminBO {

    private final MutualFundRepository fundRepo;
    private final ScenarioRepository scenarioRepo;
    private final DataEvaluationRepository dataEvaluationRepo;
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final ScenarioImpactResultRepository impactRepo;
    private final ScenarioNavSeriesRepository navSeriesRepo;
    private final NotificationBO notificationBO;

    @Autowired
    private AdminRepository adminRepo;

    public AdminBO(
            MutualFundRepository fundRepo,
            ScenarioRepository scenarioRepo,
            DataEvaluationRepository dataEvaluationRepo,
            UserRepository userRepo,
            UserProfileRepository profileRepo,
            ScenarioImpactResultRepository impactRepo,
            ScenarioNavSeriesRepository navSeriesRepo,
            NotificationBO notificationBO) {

        this.fundRepo          = fundRepo;
        this.scenarioRepo      = scenarioRepo;
        this.dataEvaluationRepo = dataEvaluationRepo;
        this.userRepo          = userRepo;
        this.profileRepo       = profileRepo;
        this.impactRepo        = impactRepo;
        this.navSeriesRepo     = navSeriesRepo;
        this.notificationBO    = notificationBO;
    }

    /* ============================
       DASHBOARD
       ============================ */

    public AdminDashboardDTO getDashboard() {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalFunds(adminRepo.getTotalFunds());
        dto.setActiveUsers(adminRepo.getActiveUsers());
        dto.setPendingComplaints(adminRepo.getPendingComplaints());
        dto.setResolvedComplaints(adminRepo.getResolvedComplaints());   // ✅ NEW
        dto.setPendingRequests(adminRepo.getPendingRequests());
        return dto;
    }

    /* ============================
       MUTUAL FUND MANAGEMENT
       ============================ */

    public void addMutualFund(AddMutualFundDTO dto) {
        MutualFundProduct fund = new MutualFundProduct();
        fund.setFundId(dto.getFundId());
        fund.setFundName(dto.getFundName());
        fund.setCategoryName(dto.getCategory());   // ✅ DTO field "category" → entity "categoryName"
        fund.setNavLevel(dto.getNav());             // ✅ DTO field "nav" → entity "navLevel"
        fund.setRisk(dto.getRisk());
        fund.setStatus(dto.getStatus());
        fund.setPromotionStatus("NORMAL");
        fund.setCreatedAt(LocalDate.now());
        fundRepo.save(fund);
    }

    public List<MutualFundProduct> getAllMutualFunds() {
        return fundRepo.findAll();
    }

    public void updateMutualFund(UpdateMutualFundDTO dto) {
        MutualFundProduct fund = fundRepo.findById(dto.getFundId())
                .orElseThrow(() -> new DataNotFoundException("Fund not found: " + dto.getFundId()));
        fund.setFundName(dto.getFundName());
        fund.setCategoryName(dto.getCategory());   // ✅ DTO "category" → entity "categoryName"
        fund.setNavLevel(dto.getNav());             // ✅ DTO "nav" → entity "navLevel"
        fund.setRisk(dto.getRisk());
        fund.setStatus(dto.getStatus());
        fundRepo.save(fund);
    }

    public void deleteMutualFund(String fundId) {
        fundRepo.deleteById(fundId);
    }

    public void promoteFund(String fundId) {
        MutualFundProduct fund = fundRepo.findById(fundId)
                .orElseThrow(() -> new DataNotFoundException("Fund not found"));
        fund.setPromotionStatus("PROMOTED");
        fundRepo.save(fund);

        // ✅ Let investors know about the newly promoted fund
        notificationBO.createNotificationForRole(
                "INVESTOR",
                "FUND_PROMOTED",
                "⭐ " + fund.getFundName() + " is now a promoted fund. "
                        + "Check it out on the Mutual Funds page.");
    }

    public void demoteFund(String fundId) {
        MutualFundProduct fund = fundRepo.findById(fundId)
                .orElseThrow(() -> new DataNotFoundException("Fund not found"));
        fund.setPromotionStatus("NORMAL");
        fundRepo.save(fund);
    }

    /* ============================
       SCENARIO MANAGEMENT
       ============================ */

    public void addScenario(ScenarioDTO dto) {
        ScenarioAnalysis scenario = new ScenarioAnalysis();
        scenario.setScenarioId(dto.getScenarioId());
        scenario.setScenarioName(dto.getScenarioName());
        scenario.setScenarioDate(dto.getEffectiveDate());
        scenario.setStatus("PENDING");
        scenario.setAction(dto.getAction() != null ? dto.getAction() : "HOLD");
        scenario.setDescription(dto.getDescription());
        scenarioRepo.save(scenario);
    }

    public List<ScenarioAnalysis> getAllScenarios() {
        List<ScenarioAnalysis> list = scenarioRepo.findAll();
        if (list.isEmpty()) return Collections.emptyList();
        return list;
    }

    public void updateScenario(ScenarioDTO dto) {
        ScenarioAnalysis scenario = scenarioRepo.findById(dto.getScenarioId())
                .orElseThrow(() -> new DataNotFoundException("Scenario not found"));
        scenario.setScenarioName(dto.getScenarioName());
        scenario.setScenarioDate(dto.getEffectiveDate());
        scenario.setAction(dto.getAction() != null ? dto.getAction() : scenario.getAction());
        scenario.setDescription(dto.getDescription());
        scenarioRepo.save(scenario);
    }

    public void deleteScenario(String scenarioId) {
        scenarioRepo.deleteScenario(scenarioId);
    }

    /* ============================
       EVALUATION APPROVAL
       ============================ */

    public List<DataEvaluation> viewPendingEvaluations() {
        try {
            return dataEvaluationRepo.findByStatusAndEvaluatorRole("PENDING", "ADMIN");
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

public void approveEvaluation(String evaluationId, String adminRemarks) {
    dataEvaluationRepo.updateEvaluationStatus(evaluationId, "APPROVED");
    dataEvaluationRepo.updateAdminRemarks(evaluationId, adminRemarks);

    // ✅ Notify the BA who submitted this evaluation
    dataEvaluationRepo.findById(evaluationId).ifPresent(ev ->
        notificationBO.createNotification(
            ev.getSubmittedBy(),
            "EVALUATION_APPROVED",
            "✅ Your scenario evaluation (" + ev.getScenarioId()
                    + ") has been APPROVED by Admin."
                    + (adminRemarks != null && !adminRemarks.isBlank()
                        ? " Remarks: " + adminRemarks : "")));
}

public void rejectEvaluation(String evaluationId, String adminRemarks) {
    dataEvaluationRepo.updateEvaluationStatus(evaluationId, "REJECTED");
    dataEvaluationRepo.updateAdminRemarks(evaluationId, adminRemarks);

    // ✅ Notify the BA who submitted this evaluation
    dataEvaluationRepo.findById(evaluationId).ifPresent(ev ->
        notificationBO.createNotification(
            ev.getSubmittedBy(),
            "EVALUATION_REJECTED",
            "❌ Your scenario evaluation (" + ev.getScenarioId()
                    + ") has been REJECTED by Admin."
                    + (adminRemarks != null && !adminRemarks.isBlank()
                        ? " Remarks: " + adminRemarks : "")));
}

    /**
     * ✅ NEW: Returns full review bundle for sreview page.
     * Loads evaluation → scenario → impact results → nav series for graph.
     */
    public EvaluationReviewDTO getEvaluationReview(String evaluationId) {

        // 1. Load evaluation record
        DataEvaluation evaluation = dataEvaluationRepo.findById(evaluationId)
                .orElseThrow(() -> new DataNotFoundException("Evaluation not found: " + evaluationId));

        // 2. Load scenario linked to evaluation
        ScenarioAnalysis scenario = scenarioRepo.findById(evaluation.getScenarioId())
                .orElseThrow(() -> new DataNotFoundException("Scenario not found: " + evaluation.getScenarioId()));

        // 3. Load all impact results generated by BA for this scenario
        List<ScenarioImpactResult> impactResults =
                impactRepo.findByScenarioIdOrderByImpactId(evaluation.getScenarioId());

        // 4. ✅ FIXED: use ordered query so nav series is in correct sequence for the graph
        List<ScenarioNavSeries> navSeries = Collections.emptyList();
        if (!impactResults.isEmpty()) {
            navSeries = navSeriesRepo.findByImpactIdOrderBySequenceNo(
                    impactResults.get(0).getImpactId()
            );
        }

        return new EvaluationReviewDTO(evaluation, scenario, impactResults, navSeries);
    }

    // ✅ NEW — per-impact nav series for multi-line chart (Admin)
    public List<ScenarioNavSeries> getNavSeriesByImpact(String impactId) {
        List<ScenarioNavSeries> series = navSeriesRepo.findByImpactIdOrderBySequenceNo(impactId);
        return series != null ? series : Collections.emptyList();
    }

    /* ============================
       ADMIN PROFILE
       ============================ */

    public UserProfileDTO viewProfile(String adminId) {
    var entity = profileRepo.getProfileByUserId(adminId);
    if (entity == null) throw new DataNotFoundException("Admin profile not found");

    UserProfileDTO dto = new UserProfileDTO();
    dto.setUserId(entity.getInvestorId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setEmail(entity.getEmail());
    dto.setMobile(entity.getMobile());
    dto.setDob(entity.getDob());
    dto.setPan(entity.getPan());
    dto.setCurrentAddress(entity.getCurrentAddress());
    dto.setPermanentAddress(entity.getPermanentAddress());

    return dto;
}

    public void updateProfile(UserProfileDTO dto) {

    if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
        throw new DataNotFoundException("Invalid Admin ID");
    }

    profileRepo.updateProfile(
            dto.getUserId(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getMobile(),
            dto.getPermanentAddress(),
            dto.getCurrentAddress(),
            dto.getPan(),
            dto.getDob()
    );
}
}
