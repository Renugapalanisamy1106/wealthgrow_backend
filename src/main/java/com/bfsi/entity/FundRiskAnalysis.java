package com.bfsi.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

/**
 * Stores fund risk analysis results submitted by BA.
 * Admin reviews and approves → updates fund's riskLevel.
 */
@Entity
@Table(name = "fund_risk_analysis", schema = "bfsimf_clean")
public class FundRiskAnalysis {

    @Id
    @Column(name = "analysis_id")
    private String analysisId;

    @Column(name = "fund_id")
    private String fundId;

    @Column(name = "fund_name")
    private String fundName;

    @Column(name = "sharpe_ratio")
    private double sharpeRatio;

    @Column(name = "max_drawdown")
    private double maxDrawdown;

    @Column(name = "stability_score")
    private double stabilityScore;

    @Column(name = "volatility")
    private double volatility;

    @Column(name = "suggested_risk")
    private String suggestedRisk;   // LOW / MEDIUM / HIGH

    @Column(name = "status")
    private String status;          // PENDING / APPROVED / REJECTED

    @Column(name = "submitted_by")
    private String submittedBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "remarks")
    private String remarks;

    public FundRiskAnalysis() {}

    // Getters and Setters
    public String getAnalysisId()  { return analysisId; }
    public void setAnalysisId(String v)  { this.analysisId = v; }

    public String getFundId()  { return fundId; }
    public void setFundId(String v)  { this.fundId = v; }

    public String getFundName()  { return fundName; }
    public void setFundName(String v)  { this.fundName = v; }

    public double getSharpeRatio()  { return sharpeRatio; }
    public void setSharpeRatio(double v)  { this.sharpeRatio = v; }

    public double getMaxDrawdown()  { return maxDrawdown; }
    public void setMaxDrawdown(double v)  { this.maxDrawdown = v; }

    public double getStabilityScore()  { return stabilityScore; }
    public void setStabilityScore(double v)  { this.stabilityScore = v; }

    public double getVolatility()  { return volatility; }
    public void setVolatility(double v)  { this.volatility = v; }

    public String getSuggestedRisk()  { return suggestedRisk; }
    public void setSuggestedRisk(String v)  { this.suggestedRisk = v; }

    public String getStatus()  { return status; }
    public void setStatus(String v)  { this.status = v; }

    public String getSubmittedBy()  { return submittedBy; }
    public void setSubmittedBy(String v)  { this.submittedBy = v; }

    public LocalDate getCreatedAt()  { return createdAt; }
    public void setCreatedAt(LocalDate v)  { this.createdAt = v; }

    public String getRemarks()  { return remarks; }
    public void setRemarks(String v)  { this.remarks = v; }
}
