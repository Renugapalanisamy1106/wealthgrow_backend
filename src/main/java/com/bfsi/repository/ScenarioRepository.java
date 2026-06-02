package com.bfsi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.ScenarioAnalysis;

@Repository
public interface ScenarioRepository
        extends JpaRepository<ScenarioAnalysis, String> {

    /* ============================
       FETCH ALL SCENARIOS
       ============================ */
    List<ScenarioAnalysis> findAll();


    /* ============================
       INSERT SCENARIO
       ============================ */
    // ✅ handled via save()
    // ensure in BO: status = "PENDING"


    /* ============================
       UPDATE SCENARIO
       ============================ */
    @Modifying
    @Transactional
    @Query("""
        UPDATE ScenarioAnalysis s
        SET s.scenarioName = :scenarioName,
            s.scenarioDate = :scenarioDate,
            s.action = :description
        WHERE s.scenarioId = :scenarioId
    """)
    void updateScenario(
            @Param("scenarioId") String scenarioId,
            @Param("scenarioName") String scenarioName,
            @Param("scenarioDate") LocalDate scenarioDate,
            @Param("description") String description
    );


    /* ============================
       DELETE SCENARIO
       ============================ */
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM ScenarioAnalysis s
        WHERE s.scenarioId = :scenarioId
    """)
    void deleteScenario(
            @Param("scenarioId") String scenarioId
    );


    /* ============================
       UPDATE STATUS + ACTION
       ============================ */
    @Modifying
    @Transactional
    @Query("""
        UPDATE ScenarioAnalysis s
        SET s.status = :status,
            s.action = :action
        WHERE s.scenarioId = :scenarioId
    """)
    void updateScenarioStatusAndAction(
            @Param("scenarioId") String scenarioId,
            @Param("status") String status,
            @Param("action") String action
    );
}