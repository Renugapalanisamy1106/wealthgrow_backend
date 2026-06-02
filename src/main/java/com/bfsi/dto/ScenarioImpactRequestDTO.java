package com.bfsi.dto;
public class ScenarioImpactRequestDTO {

    // ✅ Business IDs (VARCHAR in DB)
    private String scenarioId;
    private String fundId;

    // ✅ Numeric impact values (DOUBLE PRECISION in DB)
    private double riskImpact;
    private double stabilityScore;

    private String recommendation;
    private String analysisData;

    // ✅ Default constructor (required by Spring/Jackson)
    public ScenarioImpactRequestDTO() {
    }

    // ✅ Parameterized constructor (convenience)
    public ScenarioImpactRequestDTO(
            String scenarioId,
            String fundId,
            double riskImpact,
            double stabilityScore,
            String recommendation,
            String analysisData
    ) {
        this.scenarioId = scenarioId;
        this.fundId = fundId;
        this.riskImpact = riskImpact;
        this.stabilityScore = stabilityScore;
        this.recommendation = recommendation;
        this.analysisData = analysisData;
    }

    /* =====================
       Getters & Setters
       ===================== */

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public double getRiskImpact() {
        return riskImpact;
    }

    public void setRiskImpact(double riskImpact) {
        this.riskImpact = riskImpact;
    }

    public double getStabilityScore() {
        return stabilityScore;
    }

    public void setStabilityScore(double stabilityScore) {
        this.stabilityScore = stabilityScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getAnalysisData() {
        return analysisData;
    }

    public void setAnalysisData(String analysisData) {
        this.analysisData = analysisData;
    }
}