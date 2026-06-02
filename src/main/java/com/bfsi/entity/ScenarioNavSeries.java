package com.bfsi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scenario_nav_series")
public class ScenarioNavSeries {

    @Id
    private String seriesId;

    private String impactId;
    private Double navValue;
    private String navDate;
    private int sequenceNo;

    // ✅ Getters & Setters

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getImpactId() {
        return impactId;
    }

    public void setImpactId(String impactId) {
        this.impactId = impactId;
    }

    public Double getNavValue() {
        return navValue;
    }

    public void setNavValue(Double navValue) {
        this.navValue = navValue;
    }

    public String getNavDate() {
        return navDate;
    }

    public void setNavDate(String navDate) {
        this.navDate = navDate;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
}