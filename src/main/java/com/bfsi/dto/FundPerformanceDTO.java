package com.bfsi.dto;

import java.util.List;

public class FundPerformanceDTO {

    private List<Double> normalizedNav;
    private List<String> dates;

    // ✅ NEW FIELDS (Analytics)
    private double sharpeRatio;
    private double maxDrawdown;
    private double bounceVelocity;

    // --------------------------
    // EXISTING GETTERS/SETTERS
    // --------------------------

    public List<Double> getNormalizedNav() {
        return normalizedNav;
    }

    public void setNormalizedNav(List<Double> normalizedNav) {
        this.normalizedNav = normalizedNav;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    // --------------------------
    // NEW GETTERS/SETTERS
    // --------------------------

    public double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public double getBounceVelocity() {
        return bounceVelocity;
    }

    public void setBounceVelocity(double bounceVelocity) {
        this.bounceVelocity = bounceVelocity;
    }
}