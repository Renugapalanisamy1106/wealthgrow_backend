package com.bfsi.dto;
public class ScenarioSelectionDTO {
    private String investorId;
    private String scenarioId;
    public ScenarioSelectionDTO() {}

    public ScenarioSelectionDTO(String investorId, String scenarioId) {
        this.investorId = investorId;
        this.scenarioId = scenarioId;
    }
    public String getInvestorId() {
        return investorId;
    }
    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }
    public String getScenarioId() {
        return scenarioId;
    }
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
}