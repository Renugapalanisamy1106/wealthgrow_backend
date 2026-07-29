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

    // ✅ unnecessary but harmless
    List<DataEvaluation> findAll();

    List<DataEvaluation> findByStatus(String status);

    long countByStatus(String status);

    // ✅ duplicate submission check
    Optional<DataEvaluation> findByScenarioIdAndEvaluatorRoleAndStatus(
            String scenarioId, String evaluatorRole, String status);

    // ✅ BA submissions
    List<DataEvaluation> findBySubmittedByOrderByCreatedAtDesc(String submittedBy);

    // ✅ Admin / PM filtering
    List<DataEvaluation> findByStatusAndEvaluatorRole(String status, String evaluatorRole);

    // ✅ Update status
    @Modifying
    @Transactional
    @Query("""
        UPDATE DataEvaluation d
        SET d.status = :status
        WHERE d.evaluationId = :evaluationId
    """)
    void updateEvaluationStatus(
            @Param("evaluationId") String evaluationId,
            @Param("status") String status
    );

    // ✅ ✅ ✅ NEW — UPDATE ADMIN REMARKS (IMPORTANT)
    @Modifying
    @Transactional
    @Query("""
        UPDATE DataEvaluation d
        SET d.adminRemarks = :remarks
        WHERE d.evaluationId = :evaluationId
    """)
    void updateAdminRemarks(
            @Param("evaluationId") String evaluationId,
            @Param("remarks") String remarks
    );
}
