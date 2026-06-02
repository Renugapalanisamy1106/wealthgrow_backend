package com.bfsi.dto;

/**
 * DTO used by Investor to raise or update a complaint
 */
public class RaiseComplaintDTO {

    private String investorId;
    private String category;
    private String description;

    // ✅ Mandatory default constructor (Spring requires this)
    public RaiseComplaintDTO() {
    }

    // ✅ Convenience constructor
    public RaiseComplaintDTO(String investorId,
                             String category,
                             String description) {
        this.investorId = investorId;
        this.category = category;
        this.description = description;
    }

    /* =====================
       Getters & Setters
       ===================== */

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RaiseComplaintDTO{" +
                "investorId='" + investorId + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}