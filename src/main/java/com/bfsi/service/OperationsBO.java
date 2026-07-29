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
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.exception.InvalidOperationException;
import com.bfsi.repository.AlertRepository;
import com.bfsi.repository.NotificationRepository;
import com.bfsi.repository.TransactionRepository;
import com.bfsi.repository.PortfolioRepository;
import com.bfsi.repository.MutualFundRepository;
import com.bfsi.repository.UnitAllocationRepository;

@Service
public class OperationsBO {

    private final TransactionRepository transactionRepo;
    private final AlertRepository alertRepo;
    private final NotificationRepository notificationRepo;
    private final PortfolioRepository portfolioRepo;
    private final UnitAllocationRepository unitAllocationRepo;
    private final MutualFundRepository fundRepo;

    public OperationsBO(TransactionRepository transactionRepo,
                        AlertRepository alertRepo,
                        NotificationRepository notificationRepo,
                        PortfolioRepository portfolioRepo,
                        UnitAllocationRepository unitAllocationRepo,
                        MutualFundRepository fundRepo) {

        this.transactionRepo = transactionRepo;
        this.alertRepo = alertRepo;
        this.notificationRepo = notificationRepo;
        this.portfolioRepo = portfolioRepo;
        this.unitAllocationRepo = unitAllocationRepo;
        this.fundRepo = fundRepo;
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
            throw new DataNotFoundException("Transaction not found: " + transactionId);
        }

        // ✅ Guard: only PENDING invest transactions can be allocated (no double-allocation)
        if (!"PENDING".equalsIgnoreCase(txn.getStatus())) {
            throw new InvalidOperationException(
                "Transaction " + transactionId + " is not pending allocation (status: "
                + txn.getStatus() + ").");
        }

        if (nav <= 0) {
            throw new InvalidOperationException("NAV must be greater than zero.");
        }

        int units = (int) (txn.getAmount() / nav);
        if (units <= 0) {
            throw new InvalidOperationException("Amount too small to allocate any units at this NAV.");
        }
        double value = units * nav;

        // ✅ Create or merge the investor's portfolio holding at allocation time.
        //    Use a list lookup so pre-existing duplicate rows don't break allocation.
        java.util.List<com.bfsi.entity.Portfolio> existing =
                portfolioRepo.findAllByInvestorIdAndFundId(txn.getInvestorId(), txn.getFundId());

        com.bfsi.entity.Portfolio holding =
                (existing != null && !existing.isEmpty()) ? existing.get(0) : null;

        if (holding == null) {
            holding = new com.bfsi.entity.Portfolio(
                    UUID.randomUUID().toString(),
                    txn.getInvestorId(),
                    txn.getFundId(),
                    units,
                    value,
                    LocalDate.now()
            );
        } else {
            holding.setUnitBalance(holding.getUnitBalance() + units);
            holding.setCurrentValue(holding.getCurrentValue() + value);
        }
        portfolioRepo.save(holding);

        // ✅ Mark transaction allocated and record the unit allocation
        txn.setStatus("ALLOCATED");
        transactionRepo.save(txn);

        UnitAllocation ua = new UnitAllocation();
        ua.setAllocationId(UUID.randomUUID().toString());
        ua.setTransactionId(transactionId);
        ua.setUnits(units);
        ua.setNav(nav);
        ua.setAllocationDate(LocalDate.now());
        unitAllocationRepo.save(ua);

        // ✅ Notify investor that units are now allocated
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(txn.getInvestorId());
        notification.setType("NAV_ASSIGN");
        notification.setMessage("✅ " + units + " units allocated at NAV ₹"
                + String.format("%.2f", nav) + " for your investment of ₹" + txn.getAmount() + ".");
        notification.setStatus("UNREAD");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepo.save(notification);
    }

    /* ============================
       PENDING INVEST TRANSACTIONS (awaiting allocation)
       ============================ */
    public List<Transaction> getPendingInvestTransactions() {
        List<Transaction> list = transactionRepo.findPendingInvestTransactions();
        return (list != null) ? list : java.util.Collections.emptyList();
    }

    /* ============================
       AUTO-ALLOCATE FALLBACK
       Allocates any PENDING invest transaction older than `thresholdDays`
       using the fund's current NAV, so investors are never stuck pending.
       Invoked by the scheduler (see AllocationScheduler) and can also be
       triggered manually. Returns the number of transactions allocated.
       ============================ */
    public int autoAllocateStalePending(int thresholdDays) {
        List<Transaction> pending = transactionRepo.findPendingInvestTransactions();
        if (pending == null || pending.isEmpty()) {
            return 0;
        }

        LocalDate cutoff = LocalDate.now().minusDays(thresholdDays);
        int allocatedCount = 0;

        for (Transaction txn : pending) {
            // Only allocate ones that have waited past the threshold
            if (txn.getTxnDate() != null && txn.getTxnDate().isAfter(cutoff)) {
                continue;
            }

            MutualFundProduct fund = fundRepo.findByFundId(txn.getFundId());
            double nav = (fund != null) ? fund.getNavLevel() : 0;
            if (nav <= 0) {
                // Skip if we can't determine a valid NAV; leave it for Operations
                continue;
            }

            try {
                allocateUnits(txn.getTxnId(), nav);
                allocatedCount++;
            } catch (Exception e) {
                // Don't let one bad txn stop the batch
                System.out.println("Auto-allocate skipped txn " + txn.getTxnId()
                        + ": " + e.getMessage());
            }
        }
        return allocatedCount;
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