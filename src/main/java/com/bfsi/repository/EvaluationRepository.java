package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.Evaluation;

@Repository
public interface EvaluationRepository
        extends JpaRepository<Evaluation, String> {

    /* ============================
       SAVE EVALUATION
       ============================ */
    // ✅ handled by save()


    /* ============================
       FETCH ALL
       ============================ */
    List<Evaluation> findAll();


    /* ============================
       FETCH BY STATUS
       ============================ */
    List<Evaluation> findByStatus(String status);


    /* ============================
       UPDATE STATUS
       ============================ */
    @Modifying
    @Transactional
    @Query("""
        UPDATE Evaluation e
        SET e.status = :status
        WHERE e.evaluationId = :evaluationId
    """)
    void updateEvaluationStatus(
            @Param("evaluationId") String evaluationId,
            @Param("status") String status
    );
}