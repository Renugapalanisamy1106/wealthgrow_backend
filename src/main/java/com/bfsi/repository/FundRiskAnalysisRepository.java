package com.bfsi.repository;

import com.bfsi.entity.FundRiskAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundRiskAnalysisRepository extends JpaRepository<FundRiskAnalysis, String> {

    List<FundRiskAnalysis> findByStatus(String status);

    List<FundRiskAnalysis> findBySubmittedByOrderByCreatedAtDesc(String submittedBy);

    List<FundRiskAnalysis> findByFundId(String fundId);

    @Query("SELECT f FROM FundRiskAnalysis f ORDER BY f.createdAt DESC")
    List<FundRiskAnalysis> findAllOrderByCreatedAtDesc();
}
