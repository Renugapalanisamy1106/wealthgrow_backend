package com.bfsi.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.bfsi.service.OperationsBO;
import com.bfsi.entity.Alert;
import com.bfsi.entity.Transaction;
import com.bfsi.entity.UnitAllocation;
import com.bfsi.entity.InvestorProfile;
import com.bfsi.entity.Notification; // ✅ ADDED
import com.bfsi.repository.UserProfileRepository;

@RestController
@RequestMapping("/operations")
@CrossOrigin(origins = {
    "http://localhost:4200",
    "https://wealthgrow-frontend.vercel.app/"
})
public class OperationsController {

    private final OperationsBO operationsBO;
    private final UserProfileRepository userProfileRepository;

    public OperationsController(OperationsBO operationsBO,
                                UserProfileRepository userProfileRepository) {
        this.operationsBO = operationsBO;
        this.userProfileRepository = userProfileRepository;
    }

    /* ============================
       SUMMARY API
       ============================ */
    @GetMapping("/summary")
    public Map<String, Integer> getSummary() {

        Map<String, Integer> map = new HashMap<>();

        map.put("total", operationsBO.getTotalTransactionsToday());
        map.put("completed", operationsBO.getCompletedTransactionsToday());
        map.put("failed", operationsBO.getFailedTransactionsToday());
        map.put("pending", operationsBO.getPendingTransactionsToday());
        map.put("redemption", operationsBO.getRedemptionRequestCount());

        return map;
    }

    /* ============================
       INDIVIDUAL SUMMARY APIs
       ============================ */

    @GetMapping("/summary/total")
    public int getTotalTransactionsToday() {
        return operationsBO.getTotalTransactionsToday();
    }

    @GetMapping("/summary/completed")
    public int getCompletedTransactionsToday() {
        return operationsBO.getCompletedTransactionsToday();
    }

    @GetMapping("/summary/failed")
    public int getFailedTransactionsToday() {
        return operationsBO.getFailedTransactionsToday();
    }

    @GetMapping("/summary/pending")
    public int getPendingTransactionsToday() {
        return operationsBO.getPendingTransactionsToday();
    }

    @GetMapping("/summary/redemptions")
    public int getRedemptionRequests() {
        return operationsBO.getRedemptionRequestCount();
    }

    /* ============================
       TRANSACTION MONITOR
       ============================ */

    @GetMapping("/transactions")
    public List<Transaction> filterTransactions(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fundType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;

        return operationsBO.filterTransactions(
                transactionId,
                status,
                fundType,
                from,
                to
        );
    }

    @GetMapping("/transactions/all")
    public List<Transaction> viewAllTransactions() {
        return operationsBO.viewAllTransactions();
    }

    @GetMapping("/transactions/status/{status}")
    public List<Transaction> viewTransactionsByStatus(@PathVariable String status) {
        return operationsBO.viewTransactionsByStatus(status);
    }

    /* ============================
       ALERT PANEL
       ============================ */

    @PostMapping("/alerts")
    public String sendAlert(
            @RequestParam String transactionId,
            @RequestParam String alertType,
            @RequestParam String issueCategory,
            @RequestParam String remarks) {

        operationsBO.sendTransactionAlert(
                transactionId,
                alertType,
                issueCategory,
                remarks
        );

        return "Alert sent successfully";
    }

    @GetMapping("/alerts")
    public List<Alert> getAllAlerts() {
        return operationsBO.viewAllAlerts();
    }

    @GetMapping("/alerts/{transactionId}")
    public List<Alert> getAlertsByTransaction(@PathVariable String transactionId) {
        return operationsBO.viewAlertsByTransactionId(transactionId);
    }

    /* ============================
       NAV CALCULATOR
       ============================ */

    @PostMapping("/nav/calculate")
    public double calculateNAV(
            @RequestParam double totalAssets,
            @RequestParam double totalLiabilities,
            @RequestParam double totalUnits) {

        return operationsBO.calculateNAV(
                totalAssets,
                totalLiabilities,
                totalUnits
        );
    }

    /* ============================
       NAV ALLOCATION
       ============================ */

    @PutMapping("/allocation")
    public String allocateUnits(
            @RequestParam String transactionId,
            @RequestParam double nav) {

        operationsBO.allocateUnits(transactionId, nav);
        return "Units allocated successfully";
    }

    @GetMapping("/allocation/table")
    public List<UnitAllocation> getAllocationTable() {
        return operationsBO.getAllocationTableData();
    }

    /* ============================
       PENDING INVESTMENTS (awaiting allocation)
       ============================ */
    @GetMapping("/allocation/pending")
    public List<com.bfsi.entity.Transaction> getPendingAllocations() {
        return operationsBO.getPendingInvestTransactions();
    }

    /* ============================
       MANUAL AUTO-ALLOCATE TRIGGER
       Allocates all PENDING invest txns older than the threshold (default 0 = all).
       ============================ */
    @PostMapping("/allocation/auto")
    public String autoAllocate(@RequestParam(defaultValue = "0") int thresholdDays) {
        int n = operationsBO.autoAllocateStalePending(thresholdDays);
        return "Auto-allocated " + n + " transaction(s).";
    }

    /* ============================
       ✅ PROFILE APIs
       ============================ */

    @GetMapping("/profile/{userId}")
    public InvestorProfile getProfile(@PathVariable String userId) {

        return userProfileRepository.getProfileByUserId(userId);
    }

    @PutMapping("/profile")
public String updateProfile(@RequestBody InvestorProfile profile) {

    userProfileRepository.updateProfile(
            profile.getInvestorId(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getMobile(),
            profile.getPermanentAddress(),
            profile.getCurrentAddress(),
            profile.getPan(),
            profile.getDob()
    );

    return "Profile updated successfully";
}

    /* ============================
       ✅ ✅ OPERATION NOTIFICATIONS (NEW 🔥)
       ============================ */

    @GetMapping("/notifications/{userId}")
    public List<Notification> getNotificationsForUser(@PathVariable String userId) {
        return operationsBO.getNotificationsForUser(userId);
    }

    // Keep the original (returns all, used for general ops queries)
    @GetMapping("/notifications")
    public List<Notification> getOperationNotifications() {
        return operationsBO.getOperationNotifications();
    }

    // ✅ Mark single notification as read
    @PutMapping("/notifications/{notificationId}/read")
    public String markNotificationRead(@PathVariable String notificationId) {
        operationsBO.markNotificationRead(notificationId);
        return "Notification marked as read";
    }

    // ✅ Mark all notifications as read for a user
    @PutMapping("/notifications/read-all/{userId}")
    public String markAllNotificationsRead(@PathVariable String userId) {
        operationsBO.markAllNotificationsRead(userId);
        return "All notifications marked as read";
    }

}