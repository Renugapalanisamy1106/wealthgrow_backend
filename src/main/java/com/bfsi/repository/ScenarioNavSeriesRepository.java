package com.bfsi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.bfsi.entity.ScenarioNavSeries;

// ✅ FIXED: @Repository was missing — Spring could not inject this bean
@Repository
public interface ScenarioNavSeriesRepository
        extends JpaRepository<ScenarioNavSeries, String> {

    List<ScenarioNavSeries> findByImpactId(String impactId);

    // ✅ Order by sequenceNo for correct graph plotting
    List<ScenarioNavSeries> findByImpactIdOrderBySequenceNo(String impactId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ScenarioNavSeries s WHERE s.impactId = :impactId")
    void deleteByImpactId(@Param("impactId") String impactId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM ScenarioNavSeries s
        WHERE s.impactId IN (
            SELECT i.impactId FROM ScenarioImpactResult i WHERE i.scenarioId = :scenarioId
        )
    """)
    void deleteByScenarioId(@Param("scenarioId") String scenarioId);
}
