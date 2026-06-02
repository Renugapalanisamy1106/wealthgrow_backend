package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * Entity representing mutual_fund_product table
 */
@Entity
@Table(name = "mutualfund_product", schema = "bfsimf_clean")
public class MutualFundProduct {

    @Id
    @Column(name = "fund_id")
    private String fundId;

    @Column(name = "fund_name")
    private String fundName;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "nav_level")
    private double navLevel;

    @Column(name = "risk")
    private String risk;

    @Column(name = "status")
    private String status;

    @Column(name = "promotion_status")
    private String promotionStatus;

    @Column(name = "created_at")
    private LocalDate createdAt;

    // ✅ Default constructor (REQUIRED for JPA)
    public MutualFundProduct() {
    }

    // ✅ Parameterized constructor
    public MutualFundProduct(String fundId,
                             String fundName,
                             String categoryName,
                             double navLevel,
                             String risk,
                             String status,
                             String promotionStatus,
                             LocalDate createdAt) {

        this.fundId = fundId;
        this.fundName = fundName;
        this.categoryName = categoryName;
        this.navLevel = navLevel;
        this.risk = risk;
        this.status = status;
        this.promotionStatus = promotionStatus;
        this.createdAt = createdAt;
    }

    /* =====================
       Getters and Setters
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getNavLevel() {
        return navLevel;
    }

    public void setNavLevel(double navLevel) {
        this.navLevel = navLevel;
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

    public String getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MutualFundProduct{" +
                "fundId='" + fundId + '\'' +
                ", fundName='" + fundName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", navLevel=" + navLevel +
                ", risk='" + risk + '\'' +
                ", status='" + status + '\'' +
                ", promotionStatus='" + promotionStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}