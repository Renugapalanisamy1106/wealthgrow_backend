package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * JPA Entity for scenario_analysis table
 */
@Entity
@Table(name = "scenario_analysis", schema = "bfsimf_clean")
public class ScenarioAnalysis {

    @Id
    @Column(name = "scenario_id")
    private String scenarioId;

    @Column(name = "scenario_name")
    private String scenarioName;

    @Column(name = "scenario_date")
    private LocalDate scenarioDate;

    @Column(name = "status")
    private String status;

    @Column(name = "action")
    private String action;

    @Column(name = "description")
    private String description;

    public ScenarioAnalysis() {
    }

    public ScenarioAnalysis(String scenarioId,
                            String scenarioName,
                            LocalDate scenarioDate,
                            String status,
                            String action,
                            String description) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.scenarioDate = scenarioDate;
        this.status = status;
        this.action = action;
        this.description = description;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public LocalDate getScenarioDate() {
        return scenarioDate;
    }

    public void setScenarioDate(LocalDate scenarioDate) {
        this.scenarioDate = scenarioDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}