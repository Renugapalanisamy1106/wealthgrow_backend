package com.bfsi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bfsi.entity.Transaction;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    /* ============================
       RECORD TRANSACTION
       ============================ */
    // ✅ handled by save()


    /* ============================
       FETCH BY INVESTOR
       ============================ */
    List<Transaction> findByInvestorIdOrderByTxnDateDesc(String investorId);


    /* ============================
       FETCH ALL (ORDERED)
       ============================ */
    default List<Transaction> findAllOrdered() {
        return findAll(Sort.by(Sort.Direction.DESC, "txnDate"));
    }


    /* ============================
       FETCH BY STATUS
       ============================ */
    List<Transaction> findByStatus(String status);


    /* ============================
       FETCH BY ID
       ============================ */
    Transaction findByTxnId(String txnId);

    /* ============================
       PENDING INVEST TRANSACTIONS  (auto-allocate fallback)
       ============================ */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.status = 'PENDING' AND t.txnType = 'INVEST'
        ORDER BY t.txnDate ASC
    """)
    List<Transaction> findPendingInvestTransactions();


    /* ============================
       COUNT METHODS
       ============================ */
    long countByTxnDate(LocalDate date);

    long countByStatusAndTxnDate(String status, LocalDate date);

    long countByTxnType(String type);

    long countByStatus(String status);


    /* ============================
       FILTER TRANSACTIONS
       ============================ */
    @Query("""
        SELECT t FROM Transaction t
        WHERE (:transactionId IS NULL OR t.txnId = :transactionId)
          AND (:status IS NULL OR t.status = :status)
          AND (:type IS NULL OR t.txnType = :type)
          AND (:fromDate IS NULL OR t.txnDate >= :fromDate)
          AND (:toDate IS NULL OR t.txnDate <= :toDate)
    """)
    List<Transaction> filterTransactions(
            @Param("transactionId") String transactionId,
            @Param("status") String status,
            @Param("type") String type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
    /* ============================
   ✅ INVESTOR DASHBOARD (PM)
   ============================ */

@Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.investorId = :investorId")
Double getTotalInvestment(@Param("investorId") String investorId);


@Query("SELECT COUNT(DISTINCT t.fundId) FROM Transaction t WHERE t.investorId = :investorId")
Integer getActiveFunds(@Param("investorId") String investorId);


@Query("SELECT t FROM Transaction t WHERE t.investorId = :investorId ORDER BY t.txnDate DESC")
List<Transaction> findRecentTransactions(@Param("investorId") String investorId);

}