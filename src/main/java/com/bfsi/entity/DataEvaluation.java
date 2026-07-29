package com.bfsi.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "data_evaluation", schema = "bfsimf_clean")
public class DataEvaluation {

    @Id
    @Column(name = "request_id")
    private String evaluationId;

    @Column(name = "scenario_id")
    private String scenarioId;

    @Column(name = "request_to_role")
    private String evaluatorRole;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ✅ submitted_by
    @Column(name = "submitted_by")
    private String submittedBy;

    // ✅ NEW FIELD (YOU MISSED THIS)
    @Column(name = "admin_remarks")
    private String adminRemarks;

    public DataEvaluation() {}

    // Getters and setters
    public String getEvaluationId() { return evaluationId; }
    public void setEvaluationId(String v) { this.evaluationId = v; }

    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String v) { this.scenarioId = v; }

    public String getEvaluatorRole() { return evaluatorRole; }
    public void setEvaluatorRole(String v) { this.evaluatorRole = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String v) { this.submittedBy = v; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String v) { this.adminRemarks = v; }
}