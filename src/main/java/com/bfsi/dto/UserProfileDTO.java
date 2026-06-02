package com.bfsi.dto;

import java.time.LocalDate;

public class UserProfileDTO {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private LocalDate dob;
    private String pan;
    private String address;

    /* =====================
       Constructors
       ===================== */

    public UserProfileDTO() {
    }

    public UserProfileDTO(String userId,
                          String firstName,
                          String lastName,
                          String email,
                          String mobile,
                          LocalDate dob,
                          String pan,
                          String address) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
        this.pan = pan;
        this.address = address;
    }

    /* =====================
       Getters & Setters
       ===================== */

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    // email usually not editable, but setter kept for completeness
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

    // PAN generally immutable after KYC
    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /* =====================
       toString
       ===================== */

    @Override
    public String toString() {
        return "UserProfileDTO{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", dob=" + dob +
                ", pan='" + pan + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}