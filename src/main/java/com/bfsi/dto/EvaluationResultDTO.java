package com.bfsi.dto;

/**
 * DTO representing Evaluation details for Admin view
 * Used in Admin Evaluation Approval screen
 */
public class EvaluationResultDTO {

    private String requestId;
    private String scenarioName;
    private String submittedBy;
    private String status;

    // ✅ Parameterized constructor used by AdminDAO
    public EvaluationResultDTO(String requestId,
                               String scenarioName,
                               String submittedBy,
                               String status) {
        this.requestId = requestId;
        this.scenarioName = scenarioName;
        this.submittedBy = submittedBy;
        this.status = status;
    }

    // ✅ Getters

    public String getRequestId() {
        return requestId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public String getStatus() {
        return status;
    }
}