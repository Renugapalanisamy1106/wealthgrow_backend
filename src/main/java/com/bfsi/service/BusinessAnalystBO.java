package com.bfsi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bfsi.repository.UserProfileRepository;
import com.bfsi.dto.UserProfileDTO;
import com.bfsi.dto.FundPerformanceDTO;
import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;

import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.DataEvaluationRepository;
import com.bfsi.repository.MutualFundRepository;
import com.bfsi.repository.ScenarioImpactResultRepository;
import com.bfsi.repository.ScenarioNavSeriesRepository;
import com.bfsi.repository.ScenarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BusinessAnalystBO {

    private final ScenarioRepository             scenarioRepo;
    private final MutualFundRepository           fundRepo;
    private final ScenarioImpactResultRepository impactRepo;
    private final DataEvaluationRepository       dataEvaluationRepo;
    private final UserProfileRepository          profileRepo;
    private final ScenarioNavSeriesRepository    navSeriesRepo;

    public BusinessAnalystBO(
        ScenarioRepository             scenarioRepo,
        MutualFundRepository           fundRepo,
        ScenarioImpactResultRepository impactRepo,
        DataEvaluationRepository       dataEvaluationRepo,
        UserProfileRepository          profileRepo,
        ScenarioNavSeriesRepository    navSeriesRepo) {

        this.scenarioRepo       = scenarioRepo;
        this.fundRepo           = fundRepo;
        this.impactRepo         = impactRepo;
        this.dataEvaluationRepo = dataEvaluationRepo;
        this.profileRepo        = profileRepo;
        this.navSeriesRepo      = navSeriesRepo;
    }

    /* ============================ */
    /* VIEW ALL SCENARIOS            */
    /* ✅ FIX: returns [] not throw  */
    /* ============================ */
    public List<ScenarioAnalysis> viewAllScenarios() {
        List<ScenarioAnalysis> scenarios = scenarioRepo.findAll();
        // ✅ Return empty list — do NOT throw. Frontend shows "No scenarios" message.
        return scenarios != null ? scenarios : Collections.emptyList();
    }

    /* ============================ */
    /* ACTIVE FUNDS                 */
    /* ============================ */
    public List<MutualFundProduct> getFundsForEvaluation() {
        List<MutualFundProduct> funds = fundRepo.findByStatus("ACTIVE");
        if (funds.isEmpty()) {
            throw new DataNotFoundException("No active mutual funds available for evaluation.");
        }
        return funds;
    }

    /* ============================ */
    /* BATCH CSV UPLOAD             */
    /* ============================ */
    @Transactional
    public void uploadCsvBatch(List<MultipartFile> files, String scenarioId) {
        // ✅ Delete old data for this scenario first (clean re-upload)
        navSeriesRepo.deleteByScenarioId(scenarioId);
        impactRepo.deleteByScenarioId(scenarioId);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            System.out.println("Processing: " + file.getOriginalFilename());
            String fundId = extractFundId(file.getOriginalFilename());
            processSingleFile(file, scenarioId, fundId);
        }
    }

    private void processSingleFile(MultipartFile file, String scenarioId, String fundId) {

        List<Double> navValues = new ArrayList<>();
        List<String> dates     = new ArrayList<>();

        try (BufferedReader reader =
                 new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            reader.readLine(); // skip header
            String line;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length < 2) continue;
                dates.add(columns[0].trim());
                navValues.add(Double.parseDouble(columns[1].trim()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading CSV file: " + file.getOriginalFilename(), e);
        }

        if (navValues.isEmpty()) {
            throw new RuntimeException("CSV file has no data rows: " + file.getOriginalFilename());
        }

        double       base          = navValues.get(0);
        List<Double> normalizedNav = new ArrayList<>();
        for (double nav : navValues) normalizedNav.add((nav / base) * 100);

        double sharpe   = calculateSharpeRatio(normalizedNav);
        double drawdown = calculateMaxDrawdown(normalizedNav);
        double velocity = calculateBounceVelocity(normalizedNav);

        ScenarioImpactResult result = new ScenarioImpactResult();
        result.setImpactId("IMP" + UUID.randomUUID().toString().substring(0, 8));
        result.setScenarioId(scenarioId);
        result.setFundId(fundId);
        result.setSharpeRatio(sharpe);
        result.setMaxDrawdown(drawdown);
        result.setBounceVelocity(velocity);
        result.setRiskImpact(Math.abs(drawdown));
        result.setStabilityScore(sharpe);
        result.setRecommendation("AUTO_ANALYZED");
        result.setAnalysisData("Generated from CSV upload");
        result.setApproved(false);

        impactRepo.save(result);

        for (int i = 0; i < normalizedNav.size(); i++) {
            ScenarioNavSeries series = new ScenarioNavSeries();
            series.setSeriesId("SER" + UUID.randomUUID().toString().substring(0, 8));
            series.setImpactId(result.getImpactId());
            series.setNavValue(normalizedNav.get(i));
            series.setNavDate(dates.get(i));
            series.setSequenceNo(i);
            navSeriesRepo.save(series);
        }
    }

    private String extractFundId(String filename) {
        if (filename == null) return "UNKNOWN";
        return filename.replace(".csv", "").replace(".CSV", "").toUpperCase().trim();
    }

    /* ============================ */
    /* SINGLE FILE UPLOAD           */
    /* ============================ */
    @Transactional
    public FundPerformanceDTO uploadCsv(MultipartFile file, String scenarioId, String fundId) {

        navSeriesRepo.deleteByScenarioId(scenarioId);
        impactRepo.deleteByScenarioId(scenarioId);

        List<Double> navValues = new ArrayList<>();
        List<String> dates     = new ArrayList<>();

        try (BufferedReader reader =
                 new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length < 2) continue;
                dates.add(columns[0].trim());
                navValues.add(Double.parseDouble(columns[1].trim()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading CSV file", e);
        }

        double       base          = navValues.get(0);
        List<Double> normalizedNav = new ArrayList<>();
        for (double nav : navValues) normalizedNav.add((nav / base) * 100);

        double sharpe   = calculateSharpeRatio(normalizedNav);
        double drawdown = calculateMaxDrawdown(normalizedNav);
        double velocity = calculateBounceVelocity(normalizedNav);

        ScenarioImpactResult result = new ScenarioImpactResult();
        result.setImpactId("IMP" + UUID.randomUUID().toString().substring(0, 8));
        result.setScenarioId(scenarioId);
        result.setFundId(fundId);
        result.setSharpeRatio(sharpe);
        result.setMaxDrawdown(drawdown);
        result.setBounceVelocity(velocity);
        result.setRiskImpact(Math.abs(drawdown));
        result.setStabilityScore(sharpe);
        result.setRecommendation("AUTO_ANALYZED");
        result.setAnalysisData("Generated from CSV upload");
        result.setApproved(false);
        impactRepo.save(result);

        for (int i = 0; i < normalizedNav.size(); i++) {
            ScenarioNavSeries series = new ScenarioNavSeries();
            series.setSeriesId("SER" + UUID.randomUUID().toString().substring(0, 8));
            series.setImpactId(result.getImpactId());
            series.setNavValue(normalizedNav.get(i));
            series.setNavDate(dates.get(i));
            series.setSequenceNo(i);
            navSeriesRepo.save(series);
        }

        FundPerformanceDTO dto = new FundPerformanceDTO();
        dto.setDates(dates);
        dto.setNormalizedNav(normalizedNav);
        dto.setSharpeRatio(sharpe);
        dto.setMaxDrawdown(drawdown);
        dto.setBounceVelocity(velocity);
        return dto;
    }

    /* ============================ */
    /* ANALYTICS HELPERS            */
    /* ============================ */
    private double calculateSharpeRatio(List<Double> data) {
        if (data.size() < 2) return 0;
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            returns.add((data.get(i) - data.get(i - 1)) / data.get(i - 1));
        }
        double avg      = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = returns.stream().mapToDouble(r -> Math.pow(r - avg, 2)).average().orElse(0);
        double stdDev   = Math.sqrt(variance);
        return stdDev == 0 ? 0 : avg / stdDev;
    }

    private double calculateMaxDrawdown(List<Double> data) {
        double peak       = data.get(0);
        double maxDrawdown = 0;
        for (double value : data) {
            if (value > peak) peak = value;
            double drawdown = (value - peak) / peak;
            if (drawdown < maxDrawdown) maxDrawdown = drawdown;
        }
        return maxDrawdown;
    }

    private double calculateBounceVelocity(List<Double> data) {
        int recoveryDays = 0;
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i) > data.get(i - 1)) recoveryDays++;
        }
        return (double) recoveryDays / data.size();
    }

    /* ============================ */
    /* SCENARIO IMPACT              */
    /* ============================ */
    public void saveScenarioImpactResult(
            String scenarioId, String fundId, double riskImpact,
            double stabilityScore, String recommendation, String analysisData) {

        ScenarioImpactResult result = new ScenarioImpactResult();
        result.setImpactId("IMP" + UUID.randomUUID().toString().substring(0, 8));
        result.setScenarioId(scenarioId);
        result.setFundId(fundId);
        result.setRiskImpact(riskImpact);
        result.setStabilityScore(stabilityScore);
        result.setRecommendation(recommendation);
        result.setAnalysisData(analysisData);
        result.setApproved(false);
        impactRepo.save(result);
    }

    /* ============================ */
    /* SUBMIT FOR APPROVAL          */
    /* ============================ */
    public DataEvaluation submitScenarioAnalysisForApproval(
            String scenarioId,
            String roleToApprove,
            String submittedBy) {

        
        var existing = dataEvaluationRepo
                .findByScenarioIdAndEvaluatorRoleAndStatus(scenarioId, roleToApprove, "PENDING");

        if (existing.isPresent()) {
            System.out.println("⚠️ Pending evaluation already exists for "
                    + scenarioId + " → " + roleToApprove + ". Skipping duplicate.");
            return existing.get();
        }

        DataEvaluation evaluation = new DataEvaluation();
        evaluation.setEvaluationId("REQ" + java.util.UUID.randomUUID().toString().substring(0, 8));
        evaluation.setScenarioId(scenarioId);
        evaluation.setEvaluatorRole(roleToApprove);
        evaluation.setStatus("PENDING");
        evaluation.setCreatedAt(java.time.LocalDateTime.now());
        evaluation.setSubmittedBy(submittedBy);   // ✅ GAP 3: record who submitted

        dataEvaluationRepo.save(evaluation);
        return evaluation;
    }

    public List<ScenarioImpactResult> viewScenarioImpactResults(String scenarioId) {
        List<ScenarioImpactResult> results = impactRepo.findByScenarioIdOrderByImpactId(scenarioId);
        // ✅ Return empty list not exception — frontend handles empty state
        return results != null ? results : Collections.emptyList();
    }

    /* ============================ */
    /* PROFILE                      */
    /* ============================ */
    public UserProfileDTO viewProfile(String userId) {
        var entity = profileRepo.getProfileByUserId(userId);
        if (entity == null) throw new DataNotFoundException("Profile not found");

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
            throw new DataNotFoundException("Invalid User ID");
        }
        profileRepo.updateProfile(
            dto.getUserId(), dto.getFirstName(), dto.getLastName(),
            dto.getMobile(), dto.getAddress(), dto.getPan(), dto.getDob()
        );
    }

    /* ============================ */
    /* NAV SERIES                   */
    /* ============================ */
    public List<ScenarioNavSeries> getNavSeries(String impactId) {
        List<ScenarioNavSeries> series = navSeriesRepo.findByImpactIdOrderBySequenceNo(impactId);
        return series != null ? series : Collections.emptyList();
    }

    /* ============================ */
    /* EVALUATIONS & NOTIFICATIONS  */
    /* ============================ */
    public List<ScenarioImpactResult> getPendingApprovals() {
        return impactRepo.findByApprovedFalse();
    }

    public List<DataEvaluation> getAllEvaluations(String submittedBy) {
        if (submittedBy == null || submittedBy.isBlank()) {
            return dataEvaluationRepo.findAll();
        }
        return dataEvaluationRepo.findBySubmittedByOrderByCreatedAtDesc(submittedBy);
    }

    public List<DataEvaluation> getNotifications(String submittedBy) {
        if (submittedBy == null || submittedBy.isBlank()) {
            return dataEvaluationRepo.findAll();
        }
        return dataEvaluationRepo.findBySubmittedByOrderByCreatedAtDesc(submittedBy);
    }

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("scenarios",       scenarioRepo.count());
        stats.put("funds",           impactRepo.count());
        stats.put("pendingRequests", dataEvaluationRepo.countByStatus("PENDING"));
        stats.put("approvedRequests",dataEvaluationRepo.countByStatus("APPROVED"));
        stats.put("rejectedRequests",dataEvaluationRepo.countByStatus("REJECTED"));
        return stats;
    }
}
