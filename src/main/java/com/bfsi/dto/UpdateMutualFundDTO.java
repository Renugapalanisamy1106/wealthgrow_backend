package com.bfsi.dto;

/**
 * DTO used by Admin to update an existing Mutual Fund
 */
public class UpdateMutualFundDTO {

    private String fundId;
    private String fundName;
    private String category;
    private double nav;
    private String risk;
    private String status;

    // ✅ Mandatory default constructor (Spring needs this)
    public UpdateMutualFundDTO() {
    }

    // ✅ Convenience constructor (optional, safe)
    public UpdateMutualFundDTO(String fundId,
                               String fundName,
                               String category,
                               double nav,
                               String risk,
                               String status) {
        this.fundId = fundId;
        this.fundName = fundName;
        this.category = category;
        this.nav = nav;
        this.risk = risk;
        this.status = status;
    }

    /* =====================
       Getters & Setters
       ===================== */

    public String getFundId() {
        return fundId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getNav() {
        return nav;
    }

    public void setNav(double nav) {
        this.nav = nav;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateMutualFundDTO{" +
                "fundId='" + fundId + '\'' +
                ", fundName='" + fundName + '\'' +
                ", category='" + category + '\'' +
                ", nav=" + nav +
                ", risk='" + risk + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}