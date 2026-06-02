package com.bfsi.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * JPA Entity for user_profile table
 */
@Entity
@Table(name = "user_profile", schema = "bfsimf_clean")
public class InvestorProfile {

    @Id
    @Column(name = "user_id")
    private String investorId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "pan")
    private String pan;

    @Column(name = "address")
    private String address;

    public InvestorProfile() {
    }

    public InvestorProfile(String investorId,
                           String firstName,
                           String lastName,
                           String email,
                           String mobile,
                           LocalDate dob,
                           String pan,
                           String address) {

        this.investorId = investorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
        this.pan = pan;
        this.address = address;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
