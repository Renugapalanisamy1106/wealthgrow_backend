package com.bfsi.service;

import com.bfsi.entity.FundRiskAnalysis;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.Notification;
import com.bfsi.repository.FundRiskAnalysisRepository;
import com.bfsi.repository.MutualFundRepository;
import com.bfsi.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FundRiskBO {

    private final FundRiskAnalysisRepository fundRiskRepo;
    private final MutualFundRepository       fundRepo;
    private final NotificationRepository     notificationRepo;

    public FundRiskBO(FundRiskAnalysisRepository fundRiskRepo,
                      MutualFundRepository fundRepo,
                      NotificationRepository notificationRepo) {
        this.fundRiskRepo     = fundRiskRepo;
        this.fundRepo         = fundRepo;
        this.notificationRepo = notificationRepo;
    }

    /* ============================
       ANALYSE FUND RISK FROM CSV
       ============================ */

    public FundRiskAnalysis analyseAndSave(MultipartFile file,
                                           String fundId,
                                           String submittedBy) {

        // 1. Read NAV values from CSV (date, nav columns)
        List<Double> navValues = new ArrayList<>();

        try (BufferedReader reader =
                 new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length < 2) continue;
                String raw = cols[1].trim();
                if (raw.isEmpty()) continue;
                navValues.add(Double.parseDouble(raw));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading CSV: " + e.getMessage());
        }

        if (navValues.size() < 2) {
            throw new RuntimeException("CSV must have at least 2 data rows for risk calculation.");
        }

        // 2. Normalize NAV (base = 100)
        double base = navValues.get(0);
        List<Double> norm = new ArrayList<>();
        for (double v : navValues) norm.add((v / base) * 100.0);

        // 3. Calculate metrics
        double sharpe    = calculateSharpeRatio(norm);
        double drawdown  = calculateMaxDrawdown(norm);
        double stability = calculateStabilityScore(norm);
        double volatility = calculateVolatility(norm);

        // 4. Determine risk level from metrics
        String suggestedRisk = determineRiskLevel(sharpe, drawdown, volatility);

        // 5. Resolve fund name
        String fundName = fundId;
        MutualFundProduct fund = fundRepo.findById(fundId).orElse(null);
        if (fund != null) fundName = fund.getFundName();

        // 6. Build and save analysis record
        FundRiskAnalysis analysis = new FundRiskAnalysis();
        analysis.setAnalysisId("FRA" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        analysis.setFundId(fundId);
        analysis.setFundName(fundName);
        analysis.setSharpeRatio(Math.round(sharpe * 10000.0) / 10000.0);
        analysis.setMaxDrawdown(Math.round(drawdown * 10000.0) / 10000.0);
        analysis.setStabilityScore(Math.round(stability * 10000.0) / 10000.0);
        analysis.setVolatility(Math.round(volatility * 10000.0) / 10000.0);
        analysis.setSuggestedRisk(suggestedRisk);
        analysis.setStatus("PENDING");
        analysis.setSubmittedBy(submittedBy);
        analysis.setCreatedAt(LocalDate.now());
        analysis.setRemarks("Auto-calculated from NAV CSV upload");

        FundRiskAnalysis saved = fundRiskRepo.save(analysis);

        // ✅ Notify the BA who submitted — confirms submission received
        if (submittedBy != null && !submittedBy.isBlank()) {
            Notification baNotif = new Notification();
            baNotif.setNotificationId(UUID.randomUUID().toString());
            baNotif.setUserId(submittedBy);
            baNotif.setType("FUND_RISK_SUBMITTED");
            baNotif.setMessage("Your fund risk analysis for " + fundName +
                               " has been submitted to Admin for review. Suggested risk: " +
                               suggestedRisk + ".");
            baNotif.setStatus("UNREAD");
            baNotif.setCreatedAt(LocalDateTime.now());
            notificationRepo.save(baNotif);
        }

        return saved;
    }

    /* ============================
       GET PENDING (for Admin)
       ============================ */

    public List<FundRiskAnalysis> getPendingAnalyses() {
        return fundRiskRepo.findByStatus("PENDING");
    }

    public List<FundRiskAnalysis> getAllAnalyses() {
        return fundRiskRepo.findAllOrderByCreatedAtDesc();
    }

    public List<FundRiskAnalysis> getBySubmittedBy(String userId) {
        return fundRiskRepo.findBySubmittedByOrderByCreatedAtDesc(userId);
    }

    /* ============================
       APPROVE — updates fund risk
       ============================ */

    public void approve(String analysisId) {
        FundRiskAnalysis analysis = fundRiskRepo.findById(analysisId)
            .orElseThrow(() -> new RuntimeException("Analysis not found: " + analysisId));

        // Update the fund's risk level
        MutualFundProduct fund = fundRepo.findById(analysis.getFundId())
            .orElseThrow(() -> new RuntimeException("Fund not found: " + analysis.getFundId()));

        fund.setRisk(analysis.getSuggestedRisk());
        fundRepo.save(fund);

        // Mark analysis as approved
        analysis.setStatus("APPROVED");
        fundRiskRepo.save(analysis);

        // ✅ Notify the BA who submitted
        if (analysis.getSubmittedBy() != null && !analysis.getSubmittedBy().isBlank()) {
            Notification notif = new Notification();
            notif.setNotificationId(UUID.randomUUID().toString());
            notif.setUserId(analysis.getSubmittedBy());
            notif.setType("FUND_RISK_APPROVED");
            notif.setMessage("✅ Your fund risk analysis for " + analysis.getFundName() +
                             " has been APPROVED. Fund risk level updated to " +
                             analysis.getSuggestedRisk() + ".");
            notif.setStatus("UNREAD");
            notif.setCreatedAt(LocalDateTime.now());
            notificationRepo.save(notif);
        }
    }

    /* ============================
       REJECT
       ============================ */

    public void reject(String analysisId) {
        FundRiskAnalysis analysis = fundRiskRepo.findById(analysisId)
            .orElseThrow(() -> new RuntimeException("Analysis not found: " + analysisId));
        analysis.setStatus("REJECTED");
        fundRiskRepo.save(analysis);

        // ✅ Notify the BA who submitted
        if (analysis.getSubmittedBy() != null && !analysis.getSubmittedBy().isBlank()) {
            Notification notif = new Notification();
            notif.setNotificationId(UUID.randomUUID().toString());
            notif.setUserId(analysis.getSubmittedBy());
            notif.setType("FUND_RISK_REJECTED");
            notif.setMessage("❌ Your fund risk analysis for " + analysis.getFundName() +
                             " has been REJECTED by Admin.");
            notif.setStatus("UNREAD");
            notif.setCreatedAt(LocalDateTime.now());
            notificationRepo.save(notif);
        }
    }

    /* ============================
       RISK CALCULATION HELPERS
       ============================ */

    private double calculateSharpeRatio(List<Double> data) {
        if (data.size() < 2) return 0;
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            returns.add((data.get(i) - data.get(i - 1)) / data.get(i - 1));
        }
        double avg = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = returns.stream().mapToDouble(r -> Math.pow(r - avg, 2)).average().orElse(0);
        double stdDev = Math.sqrt(variance);
        return stdDev == 0 ? 0 : avg / stdDev;
    }

    private double calculateMaxDrawdown(List<Double> data) {
        double peak = data.get(0);
        double maxDrawdown = 0;
        for (double v : data) {
            if (v > peak) peak = v;
            double dd = (v - peak) / peak;
            if (dd < maxDrawdown) maxDrawdown = dd;
        }
        return maxDrawdown;
    }

    private double calculateStabilityScore(List<Double> data) {
        // % of periods with positive returns
        int positive = 0;
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i) >= data.get(i - 1)) positive++;
        }
        return (double) positive / (data.size() - 1);
    }

    private double calculateVolatility(List<Double> data) {
        if (data.size() < 2) return 0;
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            returns.add((data.get(i) - data.get(i - 1)) / data.get(i - 1));
        }
        double avg = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = returns.stream().mapToDouble(r -> Math.pow(r - avg, 2)).average().orElse(0);
        return Math.sqrt(variance);
    }

    /**
     * Determines risk level from metrics:
     * HIGH:   volatility > 0.02  OR  drawdown < -0.15
     * MEDIUM: volatility > 0.01  OR  drawdown < -0.05
     * LOW:    otherwise
     */
    private String determineRiskLevel(double sharpe, double drawdown, double volatility) {
        if (volatility > 0.02 || drawdown < -0.15) return "HIGH";
        if (volatility > 0.01 || drawdown < -0.05) return "MEDIUM";
        return "LOW";
    }
}
