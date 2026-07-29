package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.Portfolio;
import com.bfsi.entity.User;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {

    /* ============================
       VALIDATION
       ============================ */

    boolean existsByInvestorIdAndFundId(String investorId, String fundId);

    @Query("""
        SELECT COUNT(u) > 0
        FROM User u
        WHERE u.userId = :investorId AND u.roleId = 'INVESTOR'
    """)
    boolean investorExists(@Param("investorId") String investorId);

    /* ============================
       FETCH TOTAL UNITS (SUM)
       ============================ */

    @Query("""
        SELECT COALESCE(SUM(p.unitBalance), 0)
        FROM Portfolio p
        WHERE p.investorId = :investorId AND p.fundId = :fundId
    """)
    Integer getTotalUnits(
            @Param("investorId") String investorId,
            @Param("fundId") String fundId
    );

    /* ============================
       ✅ NEW: FETCH ONLY POSITIVE UNITS ✅
       ============================ */

    @Query("""
        SELECT p.unitBalance
        FROM Portfolio p
        WHERE p.unitBalance > 0
    """)
    List<Integer> getPositiveUnits();

    /* ============================
       WITHDRAW UNITS
       ============================ */

    @Modifying
    @Transactional
    @Query("""
        UPDATE Portfolio p
        SET p.unitBalance = p.unitBalance - :units,
            p.currentValue = :remainingValue
        WHERE p.investorId = :investorId AND p.fundId = :fundId
    """)
    void withdrawUnits(
            @Param("investorId") String investorId,
            @Param("fundId") String fundId,
            @Param("units") int units,
            @Param("remainingValue") double remainingValue
    );

    /* ============================
       TOTAL PORTFOLIO VALUE
       ============================ */

    @Query("""
        SELECT COALESCE(SUM(p.currentValue), 0)
        FROM Portfolio p
        WHERE p.investorId = :investorId
    """)
    Double getTotalPortfolioValue(
            @Param("investorId") String investorId
    );

    /* ============================
       FETCH PORTFOLIO
       ============================ */

    List<Portfolio> findByInvestorId(String investorId);

    /* ============================
       FETCH A SPECIFIC HOLDING (investor + fund)
       Used by Operations allocation to merge into an existing holding.
       ============================ */
    /* ============================
       FETCH HOLDINGS (investor + fund)
       Returns a list to stay robust if duplicate rows exist.
       ============================ */
    List<Portfolio> findAllByInvestorIdAndFundId(String investorId, String fundId);
}
