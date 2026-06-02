package com.bfsi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bfsi.entity.Alert;
import com.bfsi.entity.Notification;
import com.bfsi.entity.Transaction;
import com.bfsi.entity.UnitAllocation;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.AlertRepository;
import com.bfsi.repository.NotificationRepository;
import com.bfsi.repository.TransactionRepository;
import com.bfsi.repository.PortfolioRepository;
import com.bfsi.repository.UnitAllocationRepository;

@Service
public class OperationsBO {

    private final TransactionRepository transactionRepo;
    private final AlertRepository alertRepo;
    private final NotificationRepository notificationRepo;
    private final PortfolioRepository portfolioRepo;
    private final UnitAllocationRepository unitAllocationRepo;

    public OperationsBO(TransactionRepository transactionRepo,
                        AlertRepository alertRepo,
                        NotificationRepository notificationRepo,
                        PortfolioRepository portfolioRepo,
                        UnitAllocationRepository unitAllocationRepo) {

        this.transactionRepo = transactionRepo;
        this.alertRepo = alertRepo;
        this.notificationRepo = notificationRepo;
        this.portfolioRepo = portfolioRepo;
        this.unitAllocationRepo = unitAllocationRepo;
    }

    /* ============================
       DATE
       ============================ */
    private LocalDate getToday() {
        return LocalDate.now();
    }

    /* ============================
       SUMMARY
       ============================ */

    public int getTotalTransactionsToday() {
        return (int) transactionRepo.count();
    }

    public int getCompletedTransactionsToday() {
        return (int) transactionRepo.countByStatus("SUCCESS");
    }

    public int getFailedTransactionsToday() {
        return (int) transactionRepo.countByStatusAndTxnDate("FAILED", getToday());
    }

    public int getPendingTransactionsToday() {
        return (int) transactionRepo.countByStatusAndTxnDate("PENDING", getToday());
    }

    public int getRedemptionRequestCount() {
        return (int) transactionRepo.countByTxnType("WITHDRAW");
    }

    /* ============================
       TRANSACTION MONITOR
       ============================ */

    public List<Transaction> filterTransactions(
            String transactionId,
            String status,
            String fundType,
            LocalDate fromDate,
            LocalDate toDate) {

        List<Transaction> list = transactionRepo.filterTransactions(
                transactionId,
                status,
                fundType,
                fromDate,
                toDate
        );

        if (list == null || list.isEmpty()) {
            throw new DataNotFoundException("No transactions found.");
        }

        return list;
    }

    public List<Transaction> viewAllTransactions() {

        List<Transaction> list = transactionRepo.findAllOrdered();

        if (list.isEmpty()) {
            throw new DataNotFoundException("No transactions found.");
        }

        return list;
    }

    public List<Transaction> viewTransactionsByStatus(String status) {

        List<Transaction> list = transactionRepo.findByStatus(status);

        if (list.isEmpty()) {
            throw new DataNotFoundException("No transactions found for status.");
        }

        return list;
    }

    /* ============================
       ALERTS
       ============================ */

    public void sendTransactionAlert(
            String transactionId,
            String alertType,
            String issueCategory,
            String remarks) {

        alertRepo.insertAlert(transactionId, alertType, issueCategory, remarks);

        Transaction txn = transactionRepo.findByTxnId(transactionId);

        if (txn != null) {
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(txn.getInvestorId());
            notification.setType("ALERT");
            notification.setMessage(remarks);
            notification.setStatus("UNREAD");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepo.save(notification);
        }
    }

    public List<Alert> viewAllAlerts() {

        List<Object[]> raw = alertRepo.findAllAlertsRaw();

        if (raw == null || raw.isEmpty()) {
            throw new DataNotFoundException("No alerts found.");
        }

        return raw.stream().map(r -> new Alert(
                (String) r[0],
                (String) r[1],
                (String) r[2],
                (String) r[3],
                (String) r[4]
        )).toList();
    }

    public List<Alert> viewAlertsByTransactionId(String transactionId) {

        List<Object[]> raw = alertRepo.findByTransactionIdRaw(transactionId);

        if (raw == null || raw.isEmpty()) {
            throw new DataNotFoundException("No alerts for transaction.");
        }

        return raw.stream().map(r -> new Alert(
                (String) r[0],
                (String) r[1],
                (String) r[2],
                (String) r[3],
                (String) r[4]
        )).toList();
    }

    /* ============================
       NAV CALCULATOR
       ============================ */

    public double calculateNAV(double totalAssets,
                               double totalLiabilities,
                               double totalUnits) {

        if (totalUnits <= 0) {
            throw new IllegalArgumentException("Total units must be greater than zero.");
        }

        return (totalAssets - totalLiabilities) / totalUnits;
    }

    /* ============================
       NAV ASSIGN + UNIT ALLOCATION + NOTIFICATION
       ============================ */

    public void allocateUnits(String transactionId, double nav) {

        Transaction txn = transactionRepo.findByTxnId(transactionId);

        if (txn == null) {
            System.out.println("Transaction not found");
            return;
        }

        double units = txn.getAmount() / nav;

        txn.setStatus("ALLOCATED");
        transactionRepo.save(txn);

        UnitAllocation ua = new UnitAllocation();
        ua.setAllocationId(UUID.randomUUID().toString());
        ua.setTransactionId(transactionId);
        ua.setUnits(units);
        ua.setNav(nav);
        ua.setAllocationDate(LocalDate.now());

        unitAllocationRepo.save(ua);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(txn.getInvestorId());
        notification.setType("NAV_ASSIGN");
        notification.setMessage("Units allocated: " + units);
        notification.setStatus("UNREAD");
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepo.save(notification);
    }

    /* ============================
       ✅ ✅ NEW: FILTERED OPERATION NOTIFICATIONS 🔥
       ============================ */

    public List<Notification> getOperationNotifications() {

        List<Notification> list = notificationRepo.getOperationNotifications();

        if (list == null || list.isEmpty()) {
            throw new DataNotFoundException("No operation notifications found.");
        }

        return list;
    }
    public List<Notification> getNotificationsForUser(String userId) {
        // If NotificationRepository has findByUserId use that,
        // otherwise filter in memory
        List<Notification> all = notificationRepo.findAll();
        return all.stream()
                  .filter(n -> userId.equals(n.getUserId()))
                  .collect(java.util.stream.Collectors.toList());
    }

    public void markNotificationRead(String notificationId) {
        Notification n = notificationRepo.findById(notificationId).orElse(null);
        if (n != null) {
            n.setStatus("READ");
            notificationRepo.save(n);
        }
    }

    public void markAllNotificationsRead(String userId) {
        List<Notification> notifications = notificationRepo.findAll();
        notifications.stream()
                     .filter(n -> userId.equals(n.getUserId()))
                     .forEach(n -> {
                         n.setStatus("READ");
                         notificationRepo.save(n);
                     });
    }


    /* ============================
       ✅ FIXED: ALLOCATION TABLE DATA
       ============================ */

    public List<UnitAllocation> getAllocationTableData() {

        List<UnitAllocation> list = unitAllocationRepo.findAll();

        if (list.isEmpty()) {
            throw new DataNotFoundException("No allocation data found.");
        }

        return list;
    }
}