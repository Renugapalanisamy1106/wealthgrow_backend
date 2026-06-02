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
    private String address;

    public AdminProfileDTO(String adminId,
                           String firstName,
                           String lastName,
                           String email,
                           String mobile,
                           LocalDate dob,
                           String pan,
                           String address) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
        this.pan = pan;
        this.address = address;
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

    public String getAddress() {
        return address;
    }
}