package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * JPA Entity for complaints table
 */
@Entity
@Table(name = "complaints", schema = "bfsimf_clean")
public class Complaint {

    @Id
    @Column(name = "complaint_id")
    private String complaintId;

    @Column(name = "investor_id")
    private String investorId;

    @Column(name = "category")
    private String category;

    @Column(name = "status")
    private String status;

    @Column(name = "raised_date")
    private LocalDate raisedDate;

    @Column(name = "priority")
    private String priority;   // ✅ Added

    // ✅ Default constructor (required)
    public Complaint() {
    }

    // ✅ Updated constructor
    public Complaint(String complaintId,
                     String investorId,
                     String category,
                     LocalDate raisedDate,
                     String status,
                     String priority) {

        this.complaintId = complaintId;
        this.investorId = investorId;
        this.category = category;
        this.raisedDate = raisedDate;
        this.status = status;
        this.priority = priority;
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

    public LocalDate getRaisedDate() {
        return raisedDate;
    }

    public void setRaisedDate(LocalDate raisedDate) {
        this.raisedDate = raisedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId='" + complaintId + '\'' +
                ", investorId='" + investorId + '\'' +
                ", category='" + category + '\'' +
                ", raisedDate=" + raisedDate +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}