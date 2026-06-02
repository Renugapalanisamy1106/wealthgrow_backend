package com.bfsi.entity;
import jakarta.persistence.*;

/**
 * JPA Entity for scenario_impact_result table
 */
@Entity
@Table(name = "scenario_impact_result", schema = "bfsimf_clean")
public class ScenarioImpactResult {

    @Id
    @Column(name = "impact_id")
    private String impactId;

    @Column(name = "scenario_id")
    private String scenarioId;

    @Column(name = "fund_id")
    private String fundId;

    @Column(name = "risk_impact")
    private double riskImpact;

    @Column(name = "stability_score")
    private double stabilityScore;

    @Column(name = "recommendation")
    private String recommendation;

    @Column(name = "analysis_data")
    private String analysisData;

    @Column(name = "approved")
    private boolean approved;

    /* ✅ NEW FIELDS (ANALYTICS) */

    @Column(name = "sharpe_ratio")
    private Double sharpeRatio;

    @Column(name = "max_drawdown")
    private Double maxDrawdown;

    @Column(name = "bounce_velocity")
    private Double bounceVelocity;

    /* ============================
       CONSTRUCTORS
       ============================ */

    public ScenarioImpactResult() {
    }

    public ScenarioImpactResult(String impactId,
                               String scenarioId,
                               String fundId,
                               double riskImpact,
                               double stabilityScore,
                               String recommendation,
                               String analysisData,
                               boolean approved) {

        this.impactId = impactId;
        this.scenarioId = scenarioId;
        this.fundId = fundId;
        this.riskImpact = riskImpact;
        this.stabilityScore = stabilityScore;
        this.recommendation = recommendation;
        this.analysisData = analysisData;
        this.approved = approved;
    }

    /* ============================
       GETTERS & SETTERS
       ============================ */

    public String getImpactId() {
        return impactId;
    }

    public void setImpactId(String impactId) {
        this.impactId = impactId;
    }

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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /* ============================
       ✅ NEW GETTERS & SETTERS
       ============================ */

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(Double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public Double getBounceVelocity() {
        return bounceVelocity;
    }

    public void setBounceVelocity(Double bounceVelocity) {
        this.bounceVelocity = bounceVelocity;
    }
}

