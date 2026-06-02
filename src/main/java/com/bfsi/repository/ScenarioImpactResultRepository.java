package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.ScenarioImpactResult;

@Repository
public interface ScenarioImpactResultRepository
        extends JpaRepository<ScenarioImpactResult, String> {

    // ✅ FIXED: returns LIST instead of Optional (handles duplicates safely)
    List<ScenarioImpactResult> findByScenarioIdAndFundId(
            String scenarioId,
            String fundId
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE ScenarioImpactResult s
        SET s.approved = :approved
        WHERE s.impactId = :impactId
    """)
    void updateApprovalStatus(
            @Param("impactId") String impactId,
            @Param("approved") boolean approved
    );
    @Modifying
@Transactional
@Query("DELETE FROM ScenarioImpactResult s WHERE s.scenarioId = :scenarioId")
void deleteByScenarioId(@Param("scenarioId") String scenarioId);

    List<ScenarioImpactResult> findByScenarioIdAndApprovedTrueOrderByImpactId(String scenarioId);
    List<ScenarioImpactResult> findByScenarioIdOrderByImpactId(String scenarioId);
    List<ScenarioImpactResult> findByApprovedFalse();
    long countByScenarioId(String scenarioId);
}