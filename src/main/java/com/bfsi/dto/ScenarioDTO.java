package com.bfsi.dto;

import java.time.LocalDate;

public class ScenarioDTO {

    private String scenarioId;
    private String scenarioName;
    private LocalDate effectiveDate;
    private String action;          // ✅ ADDED
    private String description;

    // ✅ Default constructor
    public ScenarioDTO() {
    }

    // ✅ Parameterized constructor
    public ScenarioDTO(String scenarioId,
                       String scenarioName,
                       LocalDate effectiveDate,
                       String action,
                       String description) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.effectiveDate = effectiveDate;
        this.action = action;
        this.description = description;
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

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // ✅ ADD THESE METHODS
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

    // ✅ Updated toString()
    @Override
    public String toString() {
        return "ScenarioDTO{" +
                "scenarioId='" + scenarioId + '\'' +
                ", scenarioName='" + scenarioName + '\'' +
                ", effectiveDate=" + effectiveDate +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}