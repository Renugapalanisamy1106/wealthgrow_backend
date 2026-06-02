package com.bfsi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bfsi.dto.UserProfileDTO;
import com.bfsi.dto.InvestorDashboardDTO;
import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;           // ✅ NEW
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.DataEvaluationRepository;
import com.bfsi.repository.MutualFundRepository;
import com.bfsi.repository.ScenarioImpactResultRepository;
import com.bfsi.repository.ScenarioNavSeriesRepository; // ✅ NEW
import com.bfsi.repository.ScenarioRepository;
import com.bfsi.repository.UserProfileRepository;
import com.bfsi.repository.UserRepository;
import com.bfsi.repository.TransactionRepository;

@Service
public class PortfolioManagerBO {

    private final MutualFundRepository fundRepo;
    private final ScenarioRepository scenarioRepo;
    private final ScenarioImpactResultRepository impactRepo;
    private final ScenarioNavSeriesRepository navSeriesRepo;  // ✅ NEW
    private final DataEvaluationRepository dataEvaluationRepo;
    private final UserProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final TransactionRepository transactionRepo;

    public PortfolioManagerBO(MutualFundRepository fundRepo,
                              ScenarioRepository scenarioRepo,
                              ScenarioImpactResultRepository impactRepo,
                              ScenarioNavSeriesRepository navSeriesRepo,  // ✅ NEW
                              DataEvaluationRepository dataEvaluationRepo,
                              UserProfileRepository profileRepo,
                              UserRepository userRepo,
                              TransactionRepository transactionRepo) {

        this.fundRepo = fundRepo;
        this.scenarioRepo = scenarioRepo;
        this.impactRepo = impactRepo;
        this.navSeriesRepo = navSeriesRepo;   // ✅ NEW
        this.dataEvaluationRepo = dataEvaluationRepo;
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
    }

    /* ============================
       DASHBOARD
       ============================ */

    public int getTotalFunds() {
        return fundRepo.findByStatus("ACTIVE").size();
    }

    public int getActiveUsers() {
        return userRepo.countActiveUsers();
    }

    public int getPendingRequestsCount() {
        return dataEvaluationRepo.findByStatus("PENDING").size();
    }

    /* ============================
       INVESTOR LIST
       ============================ */

    public List<UserProfileDTO> getAllInvestors() {
        var list = profileRepo.findAll();
        return list.stream().map(p -> {
            UserProfileDTO dto = new UserProfileDTO();
            dto.setUserId(p.getInvestorId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            return dto;
        }).toList();
    }

    /* ============================
       INVESTOR DASHBOARD
       ============================ */

    public InvestorDashboardDTO getInvestorDashboard(String investorId) {
        InvestorDashboardDTO dto = new InvestorDashboardDTO();
        Double total = transactionRepo.getTotalInvestment(investorId);
        Integer active = transactionRepo.getActiveFunds(investorId);
        dto.setTotalValue(total != null ? total : 0);
        dto.setActiveFunds(active != null ? active : 0);
        dto.setRecentTransactions(transactionRepo.findRecentTransactions(investorId));
        return dto;
    }

    /* ============================
       MUTUAL FUNDS
       ============================ */

    public List<MutualFundProduct> viewAllFunds() {
        List<MutualFundProduct> funds = fundRepo.findByStatus("ACTIVE");
        if (funds.isEmpty()) {
            throw new DataNotFoundException("No mutual funds found.");
        }
        return funds;
    }

    /* ============================
       SCENARIOS
       ============================ */

    public List<ScenarioAnalysis> viewScenarios() {
        List<ScenarioAnalysis> scenarios = scenarioRepo.findAll();
        if (scenarios.isEmpty()) {
            throw new DataNotFoundException("No scenarios found.");
        }
        return scenarios;
    }

    // ✅ NEW — get single scenario for review page header
    public ScenarioAnalysis getScenarioById(String scenarioId) {
        return scenarioRepo.findById(scenarioId)
                .orElseThrow(() -> new DataNotFoundException("Scenario not found: " + scenarioId));
    }

    /* ============================
       EVALUATIONS
       ============================ */

    public List<DataEvaluation> getPendingEvaluationsList() {
        return dataEvaluationRepo.findByStatusAndEvaluatorRole("PENDING", "PORTFOLIO_MANAGER");
    }

    public void approveEvaluation(String evaluationId) {
        dataEvaluationRepo.updateEvaluationStatus(evaluationId, "APPROVED");
    }

    public void rejectEvaluation(String evaluationId) {
        dataEvaluationRepo.updateEvaluationStatus(evaluationId, "REJECTED");
    }

    /* ============================
       SCENARIO IMPACT
       ============================ */

    public List<ScenarioImpactResult> viewScenarioImpactForApproval(String scenarioId) {
        // ✅ FIXED: return ALL results (not only approved=true) so PM can review
        List<ScenarioImpactResult> impacts =
                impactRepo.findByScenarioIdOrderByImpactId(scenarioId);

        if (impacts.isEmpty()) {
            throw new DataNotFoundException("No scenario impact results found.");
        }
        return impacts;
    }

    public void approveScenarioImpact(String impactId) {
        impactRepo.updateApprovalStatus(impactId, true);
    }

    public void rejectScenarioImpact(String impactId) {
        impactRepo.updateApprovalStatus(impactId, false);
    }

    // ✅ NEW — NAV time-series for chart
    public List<ScenarioNavSeries> getNavSeriesByScenario(String scenarioId) {
        // Get all impact IDs for this scenario, then collect their nav series
        List<ScenarioImpactResult> impacts =
                impactRepo.findByScenarioIdOrderByImpactId(scenarioId);

        return impacts.stream()
                .flatMap(i -> navSeriesRepo.findByImpactId(i.getImpactId()).stream())
                .sorted(java.util.Comparator.comparingInt(ScenarioNavSeries::getSequenceNo))
                .toList();
    }

    // ✅ NEW — per-impact nav series (for multi-line chart, one dataset per fund)
    public List<ScenarioNavSeries> getNavSeriesByImpact(String impactId) {
        List<ScenarioNavSeries> series = navSeriesRepo.findByImpactIdOrderBySequenceNo(impactId);
        return series != null ? series : java.util.Collections.emptyList();
    }

    /* ============================
       PROFILE
       ============================ */

    public UserProfileDTO viewProfile(String pmId) {
        var entity = profileRepo.getProfileByUserId(pmId);
        if (entity == null) {
            throw new DataNotFoundException("Portfolio Manager profile not found.");
        }
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(entity.getInvestorId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setMobile(entity.getMobile());
        dto.setDob(entity.getDob());
        dto.setPan(entity.getPan());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    public void updateProfile(UserProfileDTO dto) {
        if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
            throw new DataNotFoundException("Invalid Portfolio Manager ID");
        }
        profileRepo.updateProfile(
                dto.getUserId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getMobile(),
                dto.getAddress(),
                dto.getPan(),
                dto.getDob()
        );
    }
}
