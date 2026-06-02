package com.bfsi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.DataEvaluation;

@Repository
public interface DataEvaluationRepository
        extends JpaRepository<DataEvaluation, String> {

    List<DataEvaluation> findAll();

    List<DataEvaluation> findByStatus(String status);

    long countByStatus(String status);

    // ✅ EXISTING — duplicate check before submit
    Optional<DataEvaluation> findByScenarioIdAndEvaluatorRoleAndStatus(
            String scenarioId, String evaluatorRole, String status);

    // ✅ EXISTING — BA's own submissions
    List<DataEvaluation> findBySubmittedByOrderByCreatedAtDesc(String submittedBy);

    // ✅ NEW — Admin sees only ADMIN requests, PM sees only PORTFOLIO_MANAGER requests
    List<DataEvaluation> findByStatusAndEvaluatorRole(String status, String evaluatorRole);

    @Modifying
    @Transactional
    @Query("""
        UPDATE DataEvaluation d
        SET d.status = :status
        WHERE d.evaluationId = :evaluationId
    """)
    void updateEvaluationStatus(
            @Param("evaluationId") String evaluationId,
            @Param("status") String status);
}
