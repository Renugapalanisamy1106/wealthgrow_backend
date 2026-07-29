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
    private String permanentAddress;
    private String currentAddress;

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
                          String permanentAddress,
                          String currentAddress) {

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
        this.pan = pan;
        this.permanentAddress = permanentAddress;
        this.currentAddress = currentAddress;
    }

    /* =====================
       Getters & Setters
       ===================== */

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getCurrentAddress() { return currentAddress; }
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
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
                ", permanentAddress='" + permanentAddress + '\'' +
                ", currentAddress='" + currentAddress + '\'' +
                '}';
    }
}
