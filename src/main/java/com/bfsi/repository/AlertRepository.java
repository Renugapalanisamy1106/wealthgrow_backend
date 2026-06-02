package com.bfsi.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class AlertRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /* ============================
       FETCH ALL ALERTS
       ============================ */
    public List<Object[]> findAllAlertsRaw() {
        return entityManager.createNativeQuery(
            "SELECT alert_id, txn_id, alert_type, issue_category, remarks FROM alerts"
        ).getResultList();
    }

    /* ============================
       FETCH BY TRANSACTION
       ============================ */
    public List<Object[]> findByTransactionIdRaw(String txnId) {
        return entityManager.createNativeQuery(
            "SELECT alert_id, txn_id, alert_type, issue_category, remarks FROM alerts WHERE txn_id = ?"
        )
        .setParameter(1, txnId)
        .getResultList();
    }

    /* ============================
       INSERT ALERT (FIXED ✅)
       ============================ */
    @Transactional
    public void insertAlert(String txnId,
                            String alertType,
                            String issueCategory,
                            String remarks) {

        // ✅ Generate alert id manually (safe for all DBs)
        String alertId = "ALT" + System.currentTimeMillis();

        entityManager.createNativeQuery(
            "INSERT INTO alerts (alert_id, txn_id, alert_type, issue_category, remarks) VALUES (?, ?, ?, ?, ?)"
        )
        .setParameter(1, alertId)
        .setParameter(2, txnId)
        .setParameter(3, alertType)
        .setParameter(4, issueCategory)
        .setParameter(5, remarks)
        .executeUpdate();
    }
}