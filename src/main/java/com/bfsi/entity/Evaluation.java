package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * JPA Entity for evaluation table
 */
@Entity
@Table(name = "evaluation", schema = "bfsimf_clean")
public class Evaluation {

    @Id
    @Column(name = "eval_id")
    private String evaluationId;

    @Column(name = "scenario_id")
    private String scenarioId;

    @Column(name = "submitted_by")
    private String approvedByRole;

    @Column(name = "status")
    private String status;

    @Transient
    private LocalDate approvedDate;

    public Evaluation() {
    }

    public Evaluation(String evaluationId,
                      String scenarioId,
                      String approvedByRole,
                      String status,
                      LocalDate approvedDate) {
        this.evaluationId = evaluationId;
        this.scenarioId = scenarioId;
        this.approvedByRole = approvedByRole;
        this.status = status;
        this.approvedDate = approvedDate;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getApprovedByRole() {
        return approvedByRole;
    }

    public void setApprovedByRole(String approvedByRole) {
        this.approvedByRole = approvedByRole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }
}