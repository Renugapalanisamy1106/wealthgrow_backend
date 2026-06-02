package com.bfsi.entity;

import jakarta.persistence.*;

/**
 * JPA Entity for complaint_details table
 */
@Entity
@Table(name = "complaint_details", schema = "bfsimf_clean")
public class ComplaintDetails {

    @Id
    @Column(name = "detail_id")
    private String detailId;

    @Column(name = "complaint_id")
    private String complaintId;

    @Column(name = "description")
    private String description;

    @Column(name = "resolution")
    private String resolution;

    public ComplaintDetails() {
    }

    public ComplaintDetails(String detailId,
                            String complaintId,
                            String description,
                            String resolution) {
        this.detailId = detailId;
        this.complaintId = complaintId;
        this.description = description;
        this.resolution = resolution;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}