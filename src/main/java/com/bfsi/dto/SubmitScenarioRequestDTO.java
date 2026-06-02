package com.bfsi.dto;

public class SubmitScenarioRequestDTO {

    private String scenarioId;
    private String roleToApprove;
    private String submittedBy;   // ✅ GAP 2+3: BA's userId from localStorage

    public SubmitScenarioRequestDTO() {}

    public SubmitScenarioRequestDTO(String scenarioId, String roleToApprove, String submittedBy) {
        this.scenarioId    = scenarioId;
        this.roleToApprove = roleToApprove;
        this.submittedBy   = submittedBy;
    }

    public String getScenarioId()              { return scenarioId; }
    public void setScenarioId(String v)        { this.scenarioId = v; }

    public String getRoleToApprove()           { return roleToApprove; }
    public void setRoleToApprove(String v)     { this.roleToApprove = v; }

    public String getSubmittedBy()             { return submittedBy; }
    public void setSubmittedBy(String v)       { this.submittedBy = v; }
}
