package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.*;

@Repository
public interface AdminRepository extends JpaRepository<MutualFundProduct, String> {

    /* =========================
       DASHBOARD COUNTS
       ========================= */

    @Query("SELECT COUNT(m) FROM MutualFundProduct m")
    int getTotalFunds();

    @Query("SELECT COUNT(u) FROM User u")
    int getActiveUsers();

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = 'OPEN'")
    int getPendingComplaints();

    // ✅ NEW: resolved complaints count
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = 'RESOLVED'")
    int getResolvedComplaints();

    @Query("SELECT COUNT(d) FROM DataEvaluation d WHERE d.status = 'PENDING'")
    int getPendingRequests();


    /* =========================
       MUTUAL FUNDS
       ========================= */

    @Query("SELECT m FROM MutualFundProduct m")
    List<MutualFundProduct> getAllMutualFunds();

    @Modifying
    @Transactional
    @Query("""
        UPDATE MutualFundProduct m
        SET m.promotionStatus = :status
        WHERE m.fundId = :fundId
    """)
    void updateFundPromotionStatus(
            @Param("fundId") String fundId,
            @Param("status") String status
    );


    /* =========================
       EVALUATIONS
       ========================= */

    @Query("""
        SELECT d FROM DataEvaluation d
        WHERE d.status = 'PENDING'
    """)
    List<DataEvaluation> getPendingEvaluations();

    @Modifying
    @Transactional
    @Query("""
        UPDATE DataEvaluation d
        SET d.status = :status
        WHERE d.evaluationId = :requestId
    """)
    void updateEvaluationStatus(
            @Param("requestId") String requestId,
            @Param("status") String status
    );


    /* =========================
       SCENARIOS
       ========================= */

    @Query("SELECT s FROM ScenarioAnalysis s")
    List<ScenarioAnalysis> getAllScenarios();

    @Modifying
    @Transactional
    @Query("""
        UPDATE ScenarioAnalysis s
        SET s.scenarioName = :name,
            s.scenarioDate = :date,
            s.action = :action
        WHERE s.scenarioId = :id
    """)
    void updateScenario(
            @Param("id") String scenarioId,
            @Param("name") String scenarioName,
            @Param("date") java.time.LocalDate effectiveDate,
            @Param("action") String description
    );

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM ScenarioAnalysis s
        WHERE s.scenarioId = :id
    """)
    void deleteScenario(@Param("id") String scenarioId);


    /* =========================
       ADMIN PROFILE
       ========================= */

    @Query("SELECT u FROM User u WHERE u.userId = :adminId")
    User getAdminProfile(@Param("adminId") String adminId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE User u
        SET u.email = :email
        WHERE u.userId = :adminId
    """)
    void updateAdminProfile(
            @Param("adminId") String adminId,
            @Param("email") String email
    );
}
