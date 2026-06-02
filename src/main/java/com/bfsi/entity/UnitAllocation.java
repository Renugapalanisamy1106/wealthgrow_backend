package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_allocation")
public class UnitAllocation {

    @Id
    @Column(name = "allocation_id")
    private String allocationId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "units")
    private double units;

    @Column(name = "nav")
    private double nav;

    @Column(name = "allocation_date")
    private LocalDate allocationDate;

    // ✅ Getters & Setters

    public String getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getNav() {
        return nav;
    }

    public void setNav(double nav) {
        this.nav = nav;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }
}