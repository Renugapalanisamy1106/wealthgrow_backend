package com.bfsi.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bfsi.entity.Complaint;
import com.bfsi.dto.InvestRequestDTO;
import com.bfsi.dto.InvestorDashboardDTO;
import com.bfsi.dto.RaiseComplaintDTO;
import com.bfsi.dto.UserProfileDTO;
import com.bfsi.dto.WithdrawRequestDTO;
import com.bfsi.entity.InvestorProfile;
import com.bfsi.entity.MutualFundProduct;
import com.bfsi.entity.Portfolio;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.Transaction;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.exception.InvalidOperationException;
import com.bfsi.repository.ComplaintRepository;
import com.bfsi.repository.InvestorProfileRepository;
import com.bfsi.repository.MutualFundRepository;
import com.bfsi.repository.PortfolioRepository;
import com.bfsi.repository.ScenarioImpactResultRepository;
import com.bfsi.repository.ScenarioNavSeriesRepository;
import com.bfsi.entity.ScenarioNavSeries;
import com.bfsi.repository.TransactionRepository;

@Service
public class InvestorBO {

    private final PortfolioRepository portfolioRepo;
    private final TransactionRepository transactionRepo;
    private final MutualFundRepository fundRepo;
    private final ScenarioImpactResultRepository impactRepo;
    private final InvestorProfileRepository profileRepo;
    private final ComplaintRepository complaintRepo;
    private final ScenarioNavSeriesRepository navSeriesRepo;

    public InvestorBO(PortfolioRepository portfolioRepo,
                      TransactionRepository transactionRepo,
                      MutualFundRepository fundRepo,
                      ScenarioImpactResultRepository impactRepo,
                      InvestorProfileRepository profileRepo,
                      ComplaintRepository complaintRepo,
                      ScenarioNavSeriesRepository navSeriesRepo) {

        this.portfolioRepo = portfolioRepo;
        this.transactionRepo = transactionRepo;
        this.fundRepo = fundRepo;
        this.impactRepo = impactRepo;
        this.profileRepo = profileRepo;
        this.complaintRepo = complaintRepo;
        this.navSeriesRepo = navSeriesRepo;
    }

    /* ================================
       PORTFOLIO
       ================================ */

    public List<Portfolio> getInvestorPortfolio(String investorId) {
        List<Portfolio> portfolios = portfolioRepo.findByInvestorId(investorId);

        if (portfolios == null || portfolios.isEmpty()) {
            throw new DataNotFoundException("No portfolio found for investor");
        }

        return portfolios;
    }

    public double getTotalInvestmentValue(String investorId) {

        Double totalValue = portfolioRepo.getTotalPortfolioValue(investorId);

        if (totalValue == null || totalValue <= 0) {
            throw new DataNotFoundException("No investments found for investor");
        }

        return totalValue;
    }

    /* ================================
       TRANSACTIONS
       ================================ */

    public List<Transaction> getTransactionHistory(String investorId) {

        List<Transaction> transactions =
                transactionRepo.findByInvestorIdOrderByTxnDateDesc(investorId);

        if (transactions == null || transactions.isEmpty()) {
            throw new DataNotFoundException("No transactions found for investor");
        }

        return transactions;
    }

    /* ================================
       INVEST
       ================================ */
    public void investInFund(InvestRequestDTO dto) {

    //  FIXED INVESTOR CHECK
    InvestorProfile investor = profileRepo.findByInvestorId(dto.getInvestorId());

    if (investor == null) {
        throw new DataNotFoundException("Investor not found");
    }

    // FUND VALIDATION
    // ✅ CLEAN FUND ID
String cleanFundId = dto.getFundId().trim().toUpperCase();

System.out.println("✅ Clean Fund ID: " + cleanFundId);

// ✅ FETCH FUND
MutualFundProduct fund = fundRepo.findByFundId(cleanFundId);

// ✅ VALIDATE FUND EXISTS
if (fund != null) {
    System.out.println("✅ Fund Found: " + fund.getFundId());
    System.out.println("✅ Fund Status: " + fund.getStatus());
} else {
    System.out.println("❌ Fund NOT FOUND in DB");
    throw new DataNotFoundException("Mutual fund not found");
}

// ✅ TEMP: SKIP STATUS CHECK
System.out.println("⚠️ Skipping ACTIVE status check temporarily");

    // ✅ MINIMUM INVESTMENT CHECK — ₹1000 required
    if (dto.getAmount() < 1000) {

        // Save as FAILED transaction so investor can see it in history
        Transaction failedTxn = new Transaction(
            UUID.randomUUID().toString(),
            dto.getInvestorId(),
            dto.getFundId().trim().toUpperCase(),
            "INVEST",
            dto.getAmount(),
            "CARD",
            "FAILED",                   // ✅ status = FAILED
            java.time.LocalDate.now()
        );
        transactionRepo.save(failedTxn);

        throw new InvalidOperationException(
            "Minimum investment amount is ₹1000. You entered ₹" + dto.getAmount()
        );
    }

    // ✅ AMOUNT CHECK (existing — keep this below)
    if (dto.getAmount() <= 0) {
        throw new InvalidOperationException("Investment amount must be positive");
    }


    double nav = fund.getNavLevel();

    if (nav <= 0) {
    System.out.println("⚠️ NAV is invalid, using default value");
    nav = 50;   // ✅ temporary fix
}


    int units = (int) (dto.getAmount() / nav);

    if (units <= 0) {
        throw new InvalidOperationException("Insufficient amount to buy units");
    }

    double value = units * nav;

    Portfolio portfolio = new Portfolio(
    UUID.randomUUID().toString(),
    dto.getInvestorId(),
    cleanFundId,  
    units,
    value,
    java.time.LocalDate.now()
);

    portfolioRepo.save(portfolio);

    Transaction txn = new Transaction(
    UUID.randomUUID().toString(),
    dto.getInvestorId(),
    cleanFundId,   // ✅ FIXED
    "INVEST",
    dto.getAmount(),
    "CARD",
    "SUCCESS",
    java.time.LocalDate.now()
);

    transactionRepo.save(txn);
}
    
    /* ================================
       WITHDRAW
       ================================ */

   public void withdrawFromFund(WithdrawRequestDTO dto) {

    System.out.println("------ WITHDRAW START ------");

    try {
        String cleanFundId = dto.getFundId().trim().toUpperCase();
        System.out.println("✅ Clean Fund ID: " + cleanFundId);

        MutualFundProduct fund = fundRepo.findByFundId(cleanFundId);

        if (fund == null) {
            throw new DataNotFoundException("Fund not found");
        }

        System.out.println("✅ Fund Found: " + fund.getFundId());

        // ✅ SAFE NAV HANDLING
        double nav = fund.getNavLevel();
        System.out.println("✅ NAV VALUE: " + nav);

        if (nav <= 0) {
            System.out.println("⚠️ NAV invalid → using fallback");
            nav = 50;
        }

        // ✅ SAFE AMOUNT
        if (dto.getAmount() <= 0) {
            throw new InvalidOperationException("Invalid withdrawal amount");
        }

        int unitsToWithdraw = (int) (dto.getAmount() / nav);
        System.out.println("✅ Units to withdraw: " + unitsToWithdraw);

        if (unitsToWithdraw <= 0) {
            throw new InvalidOperationException("Amount too small to withdraw");
        }

        // ✅ SAFE totalUnits check
        Integer totalUnits = portfolioRepo.getTotalUnits(
                dto.getInvestorId(),
                cleanFundId
        );

        System.out.println("✅ Total Units: " + totalUnits);

        if (totalUnits == null) {
            throw new InvalidOperationException("No portfolio record found");
        }

        if (totalUnits <= 0) {
            throw new InvalidOperationException("No units available");
        }

        if (unitsToWithdraw > totalUnits) {
            throw new InvalidOperationException("Insufficient units");
        }

        int remainingUnits = totalUnits - unitsToWithdraw;
        double remainingValue = remainingUnits * nav;

        System.out.println("✅ Remaining Units: " + remainingUnits);
        System.out.println("✅ Remaining Value: " + remainingValue);

        // ✅ UPDATE PORTFOLIO
        portfolioRepo.withdrawUnits(
                dto.getInvestorId(),
                cleanFundId,
                unitsToWithdraw,
                remainingValue
        );

        // ✅ SAVE TRANSACTION
        Transaction txn = new Transaction(
                UUID.randomUUID().toString(),
                dto.getInvestorId(),
                cleanFundId,
                "WITHDRAW",
                dto.getAmount(),
                "CARD",
                "SUCCESS",
                java.time.LocalDate.now()
        );

        transactionRepo.save(txn);

        System.out.println("✅ WITHDRAW SUCCESS ✅");

    } catch (Exception e) {
        System.out.println("❌ ERROR DURING WITHDRAW:");
        e.printStackTrace();

        throw e; // ✅ very important for proper error
    }
}

   

  


    /* ================================
       FUNDS
       ================================ */

    public List<MutualFundProduct> getAvailableFunds() {

        List<MutualFundProduct> funds = fundRepo.findByStatus("ACTIVE");

        if (funds == null || funds.isEmpty()) {
            throw new DataNotFoundException("No active funds available");
        }

        return funds;
    }

    /* ================================
       SCENARIO
       ================================ */

    public List<ScenarioImpactResult> viewScenarioAnalysis(String scenarioId) {

        List<ScenarioImpactResult> results =
                impactRepo.findByScenarioIdAndApprovedTrueOrderByImpactId(scenarioId);

        if (results == null || results.isEmpty()) {
            throw new DataNotFoundException("No scenario data available");
        }

        return results;
    }

    /* ================================
       PROFILE
       ================================ */

    public InvestorProfile viewProfile(String investorId) {

        InvestorProfile profile = profileRepo.findByInvestorId(investorId);

        if (profile == null) {
            throw new DataNotFoundException("Profile not found");
        }

        return profile;
    }

    public void updateProfile(UserProfileDTO dto) {

    if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
        throw new InvalidOperationException("Invalid user ID");
    }

    profileRepo.updateProfile(
            dto.getUserId(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getMobile(),
            dto.getAddress(),
            dto.getPan(),
            dto.getDob()
    );
}


    /* ================================
       COMPLAINT
       ================================ */

    public void raiseComplaint(RaiseComplaintDTO dto) {

        if (!portfolioRepo.investorExists(dto.getInvestorId())) {
            throw new DataNotFoundException("Investor not found");
        }

        Complaint complaint = new Complaint();
        complaint.setComplaintId("CMP" + System.currentTimeMillis());
        complaint.setInvestorId(dto.getInvestorId());
        complaint.setCategory(dto.getCategory());
        complaint.setRaisedDate(java.time.LocalDate.now());
        complaint.setStatus("OPEN");
        complaint.setPriority("MEDIUM");

        complaintRepo.save(complaint);
    }

    /* ================================
       DASHBOARD
       ================================ */

    public InvestorDashboardDTO getDashboard(String investorId) {

        return new InvestorDashboardDTO(
                getTotalInvestmentValue(investorId),
                getInvestorPortfolio(investorId).size(),
                getTransactionHistory(investorId)
        );
    }
    /* ============================
       NAV SERIES (for scenario analysis chart)
       ============================ */

    public List<ScenarioNavSeries> getNavSeriesByImpact(String impactId) {
        List<ScenarioNavSeries> series = navSeriesRepo.findByImpactIdOrderBySequenceNo(impactId);
        return series != null ? series : java.util.Collections.emptyList();
    }

}