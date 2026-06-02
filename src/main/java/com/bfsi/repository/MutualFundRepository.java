package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.MutualFundProduct;

@Repository
public interface MutualFundRepository
        extends JpaRepository<MutualFundProduct, String> {

    /* =============================
       FETCH ALL FUNDS
       ============================= */
    List<MutualFundProduct> findAll();


    /* =============================
       FETCH ACTIVE FUNDS
       ============================= */
    List<MutualFundProduct> findByStatus(String status);


    /* =============================
       FETCH FUND BY ID
       ============================= */
    MutualFundProduct findByFundId(String fundId);


    /* =============================
       INSERT FUND
       ============================= */
    // ✅ Use: save(entity)


    /* =============================
       UPDATE FUND
       ============================= */
    @Modifying
    @Transactional
    @Query("""
        UPDATE MutualFundProduct m
        SET m.fundName = :fundName,
            m.categoryName = :categoryName,
            m.navLevel = :navLevel,
            m.risk = :risk,
            m.status = :status
        WHERE m.fundId = :fundId
    """)
    void updateFund(
            @Param("fundId") String fundId,
            @Param("fundName") String fundName,
            @Param("categoryName") String categoryName,
            @Param("navLevel") double navLevel,
            @Param("risk") String risk,
            @Param("status") String status
    );


    /* =============================
       DELETE FUND
       ============================= */
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM MutualFundProduct m
        WHERE m.fundId = :fundId
    """)
    void deleteFund(@Param("fundId") String fundId);


    /* =============================
       PROMOTE / DEMOTE FUND
       ============================= */
    @Modifying
    @Transactional
    @Query("""
        UPDATE MutualFundProduct m
        SET m.promotionStatus = :promotionStatus
        WHERE m.fundId = :fundId
    """)
    void updatePromotionStatus(
            @Param("fundId") String fundId,
            @Param("promotionStatus") String promotionStatus
    );
}