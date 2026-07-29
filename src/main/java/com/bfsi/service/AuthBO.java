package com.bfsi.service;

import com.bfsi.dto.RegisterInvestorDTO;
import com.bfsi.entity.Role;
import com.bfsi.entity.User;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.exception.InvalidOperationException;
import com.bfsi.exception.UnauthorizedAccessException;
import com.bfsi.repository.InvestorProfileRepository;
import com.bfsi.repository.UserRepository;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthBO {

    private final UserRepository userRepo;
    private final InvestorProfileRepository profileRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthBO(UserRepository userRepo,
                  InvestorProfileRepository profileRepo,
                  PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /* =====================
       LOGIN
       ===================== */
    public User login(String email, String password) {

    User user = userRepo.findByEmail(email);

    if (user == null) {
        throw new UnauthorizedAccessException("Invalid email or password");
    }

    String stored = user.getPasswordHash();
    boolean ok;

    // ✅ BCrypt hashes start with $2a/$2b/$2y. If stored value is a hash,
    //    verify with the encoder. Otherwise fall back to legacy plaintext
    //    comparison and transparently upgrade the password to a hash.
    if (stored != null && stored.startsWith("$2")) {
        ok = passwordEncoder.matches(password, stored);
    } else {
        ok = stored != null && stored.equals(password);
        if (ok) {
            // upgrade-on-login: replace plaintext with a BCrypt hash
            user.setPasswordHash(passwordEncoder.encode(password));
            userRepo.save(user);
        }
    }

    if (!ok) {
        throw new UnauthorizedAccessException("Invalid email or password");
    }

    Role role = userRepo.findRoleByUserId(user.getUserId());
    if (role == null) {
        throw new DataNotFoundException("User role not found");
    }

    user.setRoleId(role.getRoleId());
    return user;
}
    /* =====================
       REGISTRATION (INVESTOR)
       ===================== */
    public User registerInvestor(RegisterInvestorDTO dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidOperationException("Passwords do not match");
        }

        if (userRepo.findByEmail(dto.getEmail()) != null) {
            throw new InvalidOperationException("Email already registered");
        }

        String userId = "INV" + UUID.randomUUID().toString().substring(0, 6);

        // ✅ Save User (password stored as BCrypt hash)
        User user = new User(
                userId,
                dto.getFirstName() + " " + dto.getLastName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                "INVESTOR"
        );
        userRepo.save(user);

        // ✅ Save Profile
        var profile = new com.bfsi.entity.InvestorProfile(
        userId,
        dto.getFirstName(),
        dto.getLastName(),
        dto.getEmail(),
        dto.getMobile(),
        null,   // dob
        null,   // pan
        null,   // currentAddress ✅
        null    // permanentAddress ✅
);
        profileRepo.save(profile);

        return new User(
                userId,
                dto.getFirstName() + " " + dto.getLastName(),
                dto.getEmail(),
                null,
                "INVESTOR"
        );
    }

    /* =====================
       FETCH USER BY EMAIL
       ===================== */
    public User getUserByEmail(String email) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new DataNotFoundException("User not found");
        }

        Role role = userRepo.findRoleByUserId(user.getUserId());
        if (role != null) {
            user.setRoleId(role.getRoleId());
        }

        return user;
    }
}