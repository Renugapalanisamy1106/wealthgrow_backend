package com.bfsi.dto;

import java.time.LocalDate;

/**
 * DTO representing Admin Profile details
 */
public class AdminProfileDTO {

    private String adminId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private LocalDate dob;
    private String pan;
    private String permanentAddress;
    private String currentAddress;

    public AdminProfileDTO(String adminId,
                           String firstName,
                           String lastName,
                           String email,
                           String mobile,
                           LocalDate dob,
                           String pan,
                           String permanentAddress,
                           String currentAddress) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
        this.pan = pan;
        this.permanentAddress = permanentAddress;
        this.currentAddress = currentAddress;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getPan() {
        return pan;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }
}
