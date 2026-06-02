package com.bfsi.dto;

public class EvaluationActionDTO {

    private String requestId;
    private String action; // APPROVED or REJECTED

    // ✅ Optional but recommended for Spring/Jackson
    public EvaluationActionDTO() {
    }

    public EvaluationActionDTO(String requestId, String action) {
        this.requestId = requestId;
        this.action = action;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getAction() {
        return action;
    }
}