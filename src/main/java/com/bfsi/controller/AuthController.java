package com.bfsi.controller;

import org.springframework.web.bind.annotation.*;
import com.bfsi.dto.LoginRequestDTO;
import com.bfsi.dto.LoginResponseDTO;
import com.bfsi.dto.RegisterInvestorDTO;
import com.bfsi.entity.User;
import com.bfsi.service.AuthBO;

/**
 * REST Controller for Authentication & Registration
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthBO authBO;

    // ✅ Constructor Injection
    public AuthController(AuthBO authBO) {
        this.authBO = authBO;
    }

    /* ============================
       LOGIN API
       ============================ */

    /**
     * Authenticates a user using email & password
     */
    @PostMapping(
            value = "/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {

        User user = authBO.login(
                request.getEmail(),
                request.getPassword()
        );

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRoleId()
        );
    }

    /* ============================
       INVESTOR REGISTRATION API
       ============================ */

    /**
     * Registers a new investor
     */
    @PostMapping(
            value = "/register",
            consumes = "application/json",
            produces = "application/json"
    )
    public LoginResponseDTO registerInvestor(
            @RequestBody RegisterInvestorDTO request) {

        User user = authBO.registerInvestor(request);

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRoleId()
        );
    }

    /* ============================
       FETCH USER BY EMAIL
       ============================ */

    /**
     * Fetch user details by email (Admin/Profile use cases)
     */
    @GetMapping("/user/{email}")
    public LoginResponseDTO getUserByEmail(
            @PathVariable String email) {

        User user = authBO.getUserByEmail(email);

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRoleId()
        );
    }
}

