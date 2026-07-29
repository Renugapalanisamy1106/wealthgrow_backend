package com.bfsi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.InvestorProfile;

@Repository
public interface InvestorProfileRepository
        extends JpaRepository<InvestorProfile, String> {

    /* ============================
       FETCH PROFILE
       ============================ */
    InvestorProfile findByInvestorId(String investorId);

    /* ============================
       UPDATE PROFILE
       ============================ */
    @Modifying
    @Transactional
    @Query("""
        UPDATE InvestorProfile p
        SET p.firstName         = :firstName,
            p.lastName          = :lastName,
            p.mobile            = :mobile,
            p.permanentAddress  = :permanentAddress,
            p.currentAddress    = :currentAddress,
            p.pan               = :pan,
            p.dob               = :dob
        WHERE p.investorId = :investorId
    """)
    void updateProfile(
            @Param("investorId") String investorId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("mobile") String mobile,
            @Param("permanentAddress") String permanentAddress,
            @Param("currentAddress") String currentAddress,
            @Param("pan") String pan,
            @Param("dob") java.time.LocalDate dob
    );
}