package com.bfsi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.InvestorProfile;

@Repository
public interface UserProfileRepository
        extends JpaRepository<InvestorProfile, String> {

    /* ============================
       FETCH PROFILE (GENERIC)
       ============================ */

    @Query("""
        SELECT p FROM InvestorProfile p
        WHERE p.investorId = :userId
    """)
    InvestorProfile getProfileByUserId(
            @Param("userId") String userId
    );

    /* ============================
       ✅ UPDATE PROFILE (ALL USERS)
       ============================ */

    @Modifying
    @Transactional
    @Query("""
        UPDATE InvestorProfile p
        SET p.firstName = :firstName,
            p.lastName = :lastName,
            p.mobile = :mobile,
            p.address = :address,
            p.pan = :pan,
            p.dob = :dob
        WHERE p.investorId = :userId
    """)
    void updateProfile(
            @Param("userId") String userId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("mobile") String mobile,
            @Param("address") String address,
            @Param("pan") String pan,
            @Param("dob") java.time.LocalDate dob
    );
}
