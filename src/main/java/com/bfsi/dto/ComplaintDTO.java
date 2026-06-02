package com.bfsi.dto;

import java.time.LocalDate;


public class ComplaintDTO {

    // ✅ Business ID must be String
    private String complaintId;
    private String investorId;
    private String category;
    private String status;
    private LocalDate raisedDate;

    // Notification-related fields
    private String message;
    private String type; // EMAIL, SMS, PUSH

    // ✅ Default constructor (Spring/Jackson safe)
    public ComplaintDTO() {
    }

    // ✅ Constructor for complaint views
    public ComplaintDTO(String complaintId,
                        String investorId,
                        String category,
                        String status,
                        LocalDate raisedDate) {

        this.complaintId = complaintId;
        this.investorId = investorId;
        this.category = category;
        this.status = status;
        this.raisedDate = raisedDate;
    }

    /* =====================
       Getters & Setters
       ===================== */

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRaisedDate() {
        return raisedDate;
    }

    public void setRaisedDate(LocalDate raisedDate) {
        this.raisedDate = raisedDate;
    }

    // =====================
    // Notification helpers
    // =====================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ComplaintDTO{" +
                "complaintId='" + complaintId + '\'' +
                ", investorId='" + investorId + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", raisedDate=" + raisedDate +
                ", type='" + type + '\'' +
                '}';
    }
}