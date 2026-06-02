package com.bfsi.service;
import com.bfsi.entity.*;
import com.bfsi.dto.*;
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
public class InvestorBOTest {

    @Mock private PortfolioRepository portfolioRepo;
    @Mock private TransactionRepository transactionRepo;
    @Mock private MutualFundRepository fundRepo;
    @Mock private ScenarioImpactResultRepository impactRepo;
    @Mock private InvestorProfileRepository profileRepo;
    @Mock private ComplaintRepository complaintRepo;

    @InjectMocks
    private InvestorBO investorBO;

    /* ================================
       PORTFOLIO
       ================================ */

    @Test
    public void testGetPortfolio_Success_INV001() {

        Portfolio p = new Portfolio("PORT001", "INV001", "MF001",
                50, 2260.50, java.time.LocalDate.now());

        when(portfolioRepo.findByInvestorId("INV001"))
                .thenReturn(List.of(p));

        List<Portfolio> result = investorBO.getInvestorPortfolio("INV001");

        assertEquals(1, result.size());
        assertEquals("INV001", result.get(0).getInvestorId());
    }

    @Test(expected = DataNotFoundException.class)
    public void testGetPortfolio_Empty() {

        when(portfolioRepo.findByInvestorId("INV999"))
                .thenReturn(Collections.emptyList());

        investorBO.getInvestorPortfolio("INV999");
    }

    /* ================================
       TOTAL VALUE
       ================================ */

    @Test
    public void testTotalInvestmentValue_INV001() {

        when(portfolioRepo.getTotalPortfolioValue("INV001"))
                .thenReturn(2260.50);

        double result =
                investorBO.getTotalInvestmentValue("INV001");

        assertEquals(2260.50, result, 0.01);
    }

    /* ================================
       TRANSACTIONS
       ================================ */

    @Test
    public void testTransactionHistory_Success() {

        Transaction txn = new Transaction(
                "TXN001", "INV001", "MF001",
                "INVEST", 5000, "CARD",
                "SUCCESS", java.time.LocalDate.now()
        );

        when(transactionRepo.findByInvestorIdOrderByTxnDateDesc("INV001"))
                .thenReturn(List.of(txn));

        List<Transaction> result =
                investorBO.getTransactionHistory("INV001");

        assertEquals(1, result.size());
    }

    /* ================================
       INVEST
       ================================ */

    @Test
public void testInvestInFund_Success() {

    InvestRequestDTO dto = new InvestRequestDTO();
    dto.setInvestorId("INV001");
    dto.setFundId("MF001");
    dto.setAmount(5000);

    // ✅ FIX: mock investor
    InvestorProfile investor = new InvestorProfile();
    investor.setInvestorId("INV001");

    when(profileRepo.findByInvestorId("INV001"))
            .thenReturn(investor);

    // ✅ FIX: mock fund
    MutualFundProduct fund = new MutualFundProduct();
    fund.setFundId("MF001");
    fund.setNavLevel(45.21);

    when(fundRepo.findByFundId("MF001"))
            .thenReturn(fund);

    investorBO.investInFund(dto);

    verify(portfolioRepo).save(any(Portfolio.class));
    verify(transactionRepo).save(any(Transaction.class));
}

    @Test(expected = InvalidOperationException.class)
public void testInvest_InvalidAmount() {

    InvestRequestDTO dto = new InvestRequestDTO();
    dto.setInvestorId("INV001");
    dto.setFundId("MF001");
    dto.setAmount(0);

    // ✅ FIX: investor must exist
    InvestorProfile investor = new InvestorProfile();
    investor.setInvestorId("INV001");

    when(profileRepo.findByInvestorId("INV001"))
            .thenReturn(investor);

    MutualFundProduct fund = new MutualFundProduct();
    fund.setFundId("MF001");
    fund.setNavLevel(10);

    when(fundRepo.findByFundId("MF001"))
            .thenReturn(fund);

    investorBO.investInFund(dto);
}

    /* ================================
       ✅ WITHDRAW (FIXED)
       ================================ */

    @Test
    public void testWithdraw_Success() {

        WithdrawRequestDTO dto = new WithdrawRequestDTO();
        dto.setInvestorId("INV001");
        dto.setFundId("MF001");
        dto.setAmount(100);

        MutualFundProduct fund = new MutualFundProduct();
        fund.setNavLevel(10);

        when(fundRepo.findByFundId("MF001")).thenReturn(fund);

        // ✅ FIXED METHOD
        when(portfolioRepo.getTotalUnits("INV001", "MF001"))
                .thenReturn(50);

        investorBO.withdrawFromFund(dto);

        verify(portfolioRepo).withdrawUnits(
                eq("INV001"),
                eq("MF001"),
                anyInt(),
                anyDouble()
        );

        verify(transactionRepo).save(any(Transaction.class));
    }

    /* ================================
       FUNDS
       ================================ */

    @Test
    public void testAvailableFunds() {

        MutualFundProduct fund = new MutualFundProduct();
        fund.setStatus("ACTIVE");

        when(fundRepo.findByStatus("ACTIVE"))
                .thenReturn(List.of(fund));

        List<MutualFundProduct> result =
                investorBO.getAvailableFunds();

        assertFalse(result.isEmpty());
    }

    /* ================================
       SCENARIO
       ================================ */

    @Test
    public void testScenarioAnalysis_Success() {

        ScenarioImpactResult result = new ScenarioImpactResult();

        when(impactRepo.findByScenarioIdAndApprovedTrueOrderByImpactId("SC001"))
                .thenReturn(List.of(result));

        List<ScenarioImpactResult> list =
                investorBO.viewScenarioAnalysis("SC001");

        assertEquals(1, list.size());
    }

    /* ================================
       PROFILE
       ================================ */

    @Test
    public void testViewProfile_Success() {

        InvestorProfile profile = new InvestorProfile();
        profile.setInvestorId("INV001");

        when(profileRepo.findByInvestorId("INV001"))
                .thenReturn(profile);

        InvestorProfile result =
                investorBO.viewProfile("INV001");

        assertNotNull(result);
    }

    /* ================================
       COMPLAINT
       ================================ */

    @Test
    public void testRaiseComplaint() {

        RaiseComplaintDTO dto = new RaiseComplaintDTO();
        dto.setInvestorId("INV001");
        dto.setCategory("Transaction Issue");
        dto.setDescription("Transaction failed");

        when(portfolioRepo.investorExists("INV001")).thenReturn(true);

        investorBO.raiseComplaint(dto);

        verify(complaintRepo).save(any(Complaint.class));
    }

    /* ================================
       DASHBOARD
       ================================ */

    @Test
    public void testDashboard() {

        when(portfolioRepo.getTotalPortfolioValue("INV001"))
                .thenReturn(2260.50);

        when(portfolioRepo.findByInvestorId("INV001"))
                .thenReturn(List.of(new Portfolio()));

        when(transactionRepo.findByInvestorIdOrderByTxnDateDesc("INV001"))
                .thenReturn(List.of(new Transaction()));

        InvestorDashboardDTO dto =
                investorBO.getDashboard("INV001");

        assertEquals(2260.50, dto.getTotalValue(), 0.01);
        assertEquals(1, dto.getActiveFunds());
    }
}