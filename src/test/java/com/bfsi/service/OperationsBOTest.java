package com.bfsi.service;

import com.bfsi.entity.*;
import com.bfsi.exception.*;
import com.bfsi.repository.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OperationsBOTest {

    @Mock private TransactionRepository transactionRepo;
    @Mock private AlertRepository alertRepo;
    @Mock private NotificationRepository notificationRepo;
    @Mock private PortfolioRepository portfolioRepo;
    @Mock private UnitAllocationRepository unitAllocationRepo;
    @Mock private MutualFundRepository fundRepo;

    @InjectMocks
    private OperationsBO operationsBO;

    /* ============================
       ✅ TRANSACTION SUMMARY (FIXED)
       ============================ */

    @Test
    public void testTotalTransactionsToday() {

        when(transactionRepo.count()).thenReturn(2L);

        int result = operationsBO.getTotalTransactionsToday();

        assertEquals(2, result);
    }

    @Test
    public void testCompletedTransactionsToday() {

        when(transactionRepo.countByStatus("SUCCESS"))
                .thenReturn(1L);

        assertEquals(1, operationsBO.getCompletedTransactionsToday());
    }

    /* ============================
       TRANSACTION MONITOR
       ============================ */

    @Test
    public void testFilterTransactions_Success() {

        when(transactionRepo.filterTransactions(
                any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(new Transaction()));

        assertFalse(
            operationsBO.filterTransactions(null, null, null, null, null)
                    .isEmpty()
        );
    }

    @Test(expected = DataNotFoundException.class)
    public void testFilterTransactions_Empty() {

        when(transactionRepo.filterTransactions(
                any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        operationsBO.filterTransactions(null, null, null, null, null);
    }

    @Test
    public void testViewAllTransactions() {

        when(transactionRepo.findAllOrdered())
                .thenReturn(Collections.singletonList(new Transaction()));

        assertFalse(operationsBO.viewAllTransactions().isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewAllTransactions_Empty() {

        when(transactionRepo.findAllOrdered())
                .thenReturn(Collections.emptyList());

        operationsBO.viewAllTransactions();
    }

    /* ============================
       ALERTS
       ============================ */

    @Test
    public void testSendTransactionAlert() {

        Transaction txn = new Transaction();
        txn.setInvestorId("INV001");

        when(transactionRepo.findByTxnId("TXN001"))
                .thenReturn(txn);

        operationsBO.sendTransactionAlert(
                "TXN001", "TYPE", "CATEGORY", "Test issue");

        verify(alertRepo).insertAlert(
                eq("TXN001"), anyString(), anyString(), anyString());

        verify(notificationRepo).save(any(Notification.class));
    }

    /* ============================
       VIEW ALERTS
       ============================ */

    @Test
    public void testViewAllAlerts() {

        List<Object[]> raw = Collections.singletonList(
                new Object[]{"A1", "TXN1", "TYPE", "CATEGORY", "Remark"}
        );

        when(alertRepo.findAllAlertsRaw()).thenReturn(raw);

        assertEquals(1, operationsBO.viewAllAlerts().size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewAllAlerts_Empty() {

        when(alertRepo.findAllAlertsRaw())
                .thenReturn(Collections.emptyList());

        operationsBO.viewAllAlerts();
    }

    @Test
    public void testViewAlertsByTransaction() {

        List<Object[]> raw = Collections.singletonList(
                new Object[]{"A1", "TXN001", "TYPE", "CATEGORY", "Remark"}
        );

        when(alertRepo.findByTransactionIdRaw("TXN001"))
                .thenReturn(raw);

        assertEquals(1,
                operationsBO.viewAlertsByTransactionId("TXN001").size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewAlertsByTransaction_Empty() {

        when(alertRepo.findByTransactionIdRaw("TXN001"))
                .thenReturn(Collections.emptyList());

        operationsBO.viewAlertsByTransactionId("TXN001");
    }

    /* ============================
       NAV CALCULATOR
       ============================ */

    @Test
    public void testCalculateNAV() {

        double result = operationsBO.calculateNAV(1000, 200, 100);

        assertEquals(8.0, result, 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateNAV_InvalidUnits() {

        operationsBO.calculateNAV(1000, 200, 0);
    }

    /* ============================
       ✅ ALLOCATION (FIXED)
       ============================ */

    @Test
    public void testAllocateUnits_Success() {

        Transaction txn = new Transaction();
        txn.setAmount(1000);
        txn.setInvestorId("INV01");
        txn.setFundId("MF001");
        txn.setStatus("PENDING");   // ✅ only pending txns can be allocated

        when(transactionRepo.findByTxnId("TXN001"))
                .thenReturn(txn);
        when(portfolioRepo.findAllByInvestorIdAndFundId("INV01", "MF001"))
                .thenReturn(java.util.Collections.emptyList());   // no existing holding → a new one is created

        operationsBO.allocateUnits("TXN001", 10);

        verify(portfolioRepo).save(any());
        verify(transactionRepo).save(any());
        verify(unitAllocationRepo).save(any());
        verify(notificationRepo).save(any());
    }

    @Test(expected = com.bfsi.exception.DataNotFoundException.class)
    public void testAllocateUnits_NotFound() {

        when(transactionRepo.findByTxnId("TXN001"))
                .thenReturn(null);

        operationsBO.allocateUnits("TXN001", 10);
    }

    /* ============================
       ALLOCATION TABLE
       ============================ */

    @Test
    public void testAllocationTable() {

        when(unitAllocationRepo.findAll())
                .thenReturn(Collections.singletonList(new UnitAllocation()));

        assertFalse(operationsBO.getAllocationTableData().isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testAllocationTable_Empty() {

        when(unitAllocationRepo.findAll())
                .thenReturn(Collections.emptyList());

        operationsBO.getAllocationTableData();
    }

    /* ============================
       ✅ EXTRA COVERAGE (NEW)
       ============================ */

    @Test
    public void testOperationNotifications() {

        when(notificationRepo.getOperationNotifications())
                .thenReturn(List.of(new Notification()));

        assertFalse(operationsBO.getOperationNotifications().isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testOperationNotifications_Empty() {

        when(notificationRepo.getOperationNotifications())
                .thenReturn(Collections.emptyList());

        operationsBO.getOperationNotifications();
    }

    /* ============================
       ✅ NEW: ALLOCATION — MERGE INTO EXISTING HOLDING
       ============================ */
    @Test
    public void testAllocateUnits_MergesExistingHolding() {

        Transaction txn = new Transaction();
        txn.setAmount(1000);
        txn.setInvestorId("INV01");
        txn.setFundId("MF001");
        txn.setStatus("PENDING");

        when(transactionRepo.findByTxnId("TXN001")).thenReturn(txn);

        Portfolio existing = new Portfolio("P1", "INV01", "MF001", 50, 5000.0,
                java.time.LocalDate.now());
        when(portfolioRepo.findAllByInvestorIdAndFundId("INV01", "MF001"))
                .thenReturn(java.util.List.of(existing));

        // NAV 10 → 100 units, value 1000 added on top of existing 50 units / 5000 value
        operationsBO.allocateUnits("TXN001", 10);

        assertEquals(150, existing.getUnitBalance());
        assertEquals(6000.0, existing.getCurrentValue(), 0.001);
        verify(portfolioRepo).save(existing);
        verify(unitAllocationRepo).save(any());
    }

    /* ============================
       ✅ NEW: ALLOCATION — REJECT NON-PENDING
       ============================ */
    @Test(expected = InvalidOperationException.class)
    public void testAllocateUnits_RejectsNonPending() {

        Transaction txn = new Transaction();
        txn.setAmount(1000);
        txn.setInvestorId("INV01");
        txn.setFundId("MF001");
        txn.setStatus("ALLOCATED");   // already allocated

        when(transactionRepo.findByTxnId("TXN001")).thenReturn(txn);

        operationsBO.allocateUnits("TXN001", 10);
    }

    /* ============================
       ✅ NEW: ALLOCATION — REJECT BAD NAV
       ============================ */
    @Test(expected = InvalidOperationException.class)
    public void testAllocateUnits_RejectsBadNav() {

        Transaction txn = new Transaction();
        txn.setAmount(1000);
        txn.setInvestorId("INV01");
        txn.setFundId("MF001");
        txn.setStatus("PENDING");

        when(transactionRepo.findByTxnId("TXN001")).thenReturn(txn);

        operationsBO.allocateUnits("TXN001", 0);   // NAV must be > 0
    }

    /* ============================
       ✅ NEW: AUTO-ALLOCATE FALLBACK
       ============================ */
    @Test
    public void testAutoAllocate_AllocatesStalePending() {

        Transaction txn = new Transaction();
        txn.setTxnId("TXN001");
        txn.setAmount(1000);
        txn.setInvestorId("INV01");
        txn.setFundId("MF001");
        txn.setStatus("PENDING");
        txn.setTxnDate(java.time.LocalDate.now().minusDays(5));  // stale

        when(transactionRepo.findPendingInvestTransactions())
                .thenReturn(List.of(txn));

        MutualFundProduct fund = new MutualFundProduct();
        fund.setFundId("MF001");
        fund.setNavLevel(10);
        when(fundRepo.findByFundId("MF001")).thenReturn(fund);

        // allocateUnits will re-fetch the txn by id
        when(transactionRepo.findByTxnId("TXN001")).thenReturn(txn);
        when(portfolioRepo.findAllByInvestorIdAndFundId("INV01", "MF001")).thenReturn(java.util.Collections.emptyList());

        int count = operationsBO.autoAllocateStalePending(1);

        assertEquals(1, count);
        verify(portfolioRepo).save(any());
    }

    @Test
    public void testAutoAllocate_SkipsRecentPending() {

        Transaction txn = new Transaction();
        txn.setTxnId("TXN001");
        txn.setStatus("PENDING");
        txn.setTxnDate(java.time.LocalDate.now());  // today → newer than cutoff

        when(transactionRepo.findPendingInvestTransactions())
                .thenReturn(List.of(txn));

        int count = operationsBO.autoAllocateStalePending(1);

        assertEquals(0, count);
        verify(portfolioRepo, never()).save(any());
    }
}