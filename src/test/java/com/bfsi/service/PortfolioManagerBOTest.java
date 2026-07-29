package com.bfsi.service;

import com.bfsi.dto.UserProfileDTO;
import com.bfsi.entity.*;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioManagerBOTest {

    @Mock private MutualFundRepository fundRepo;
    @Mock private ScenarioRepository scenarioRepo;
    @Mock private ScenarioImpactResultRepository impactRepo;
    @Mock private ScenarioNavSeriesRepository navSeriesRepo;
    @Mock private DataEvaluationRepository dataEvaluationRepo;
    @Mock private UserProfileRepository profileRepo;
    @Mock private UserRepository userRepo;
    @Mock private TransactionRepository transactionRepo;

    @Mock private NotificationBO notificationBO;

    @InjectMocks
    private PortfolioManagerBO pmBO;

    /* ============================
       DASHBOARD
       ============================ */

    @Test
    public void testDashboardMetrics() {

        when(fundRepo.findByStatus("ACTIVE"))
                .thenReturn(Collections.singletonList(new MutualFundProduct()));

        when(userRepo.countActiveUsers()).thenReturn(5);

        when(dataEvaluationRepo.findByStatus("PENDING"))
                .thenReturn(Collections.singletonList(new DataEvaluation()));

        assertEquals(1, pmBO.getTotalFunds());
        assertEquals(5, pmBO.getActiveUsers());
        assertEquals(1, pmBO.getPendingRequestsCount());
    }

    /* ============================
       FUNDS
       ============================ */

    @Test
    public void testViewAllFunds_Success() {

        when(fundRepo.findByStatus("ACTIVE"))
                .thenReturn(Collections.singletonList(new MutualFundProduct()));

        assertFalse(pmBO.viewAllFunds().isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewAllFunds_Empty() {

        when(fundRepo.findByStatus("ACTIVE"))
                .thenReturn(Collections.emptyList());

        pmBO.viewAllFunds();
    }

    /* ============================
       SCENARIOS
       ============================ */

    @Test
    public void testViewScenarios_Success() {

        when(scenarioRepo.findAll())
                .thenReturn(Collections.singletonList(new ScenarioAnalysis()));

        assertEquals(1, pmBO.viewScenarios().size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewScenarios_Empty() {

        when(scenarioRepo.findAll())
                .thenReturn(Collections.emptyList());

        pmBO.viewScenarios();
    }

    /* ============================
       ✅ EVALUATIONS (FIXED)
       ============================ */

    @Test
    public void testViewPendingEvaluations_Success() {

        when(dataEvaluationRepo
                .findByStatusAndEvaluatorRole("PENDING", "PORTFOLIO_MANAGER"))
                .thenReturn(Collections.singletonList(new DataEvaluation()));

        List<DataEvaluation> result =
                pmBO.getPendingEvaluationsList();

        assertFalse(result.isEmpty());
    }

    @Test
    public void testApproveEvaluation() {

        pmBO.approveEvaluation("REQ001");

        verify(dataEvaluationRepo)
                .updateEvaluationStatus("REQ001", "APPROVED");
    }

    @Test
    public void testRejectEvaluation() {

        pmBO.rejectEvaluation("REQ001");

        verify(dataEvaluationRepo)
                .updateEvaluationStatus("REQ001", "REJECTED");
    }

    /* ============================
       SCENARIO IMPACT (FIXED)
       ============================ */

    @Test
    public void testViewScenarioImpact_Success() {

        when(impactRepo.findByScenarioIdOrderByImpactId("SC001"))
                .thenReturn(Collections.singletonList(new ScenarioImpactResult()));

        assertEquals(1,
                pmBO.viewScenarioImpactForApproval("SC001").size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewScenarioImpact_Empty() {

        when(impactRepo.findByScenarioIdOrderByImpactId("SC001"))
                .thenReturn(Collections.emptyList());

        pmBO.viewScenarioImpactForApproval("SC001");
    }

    @Test
    public void testApproveScenarioImpact() {

        pmBO.approveScenarioImpact("IMP001");

        verify(impactRepo).updateApprovalStatus("IMP001", true);
    }

    @Test
    public void testRejectScenarioImpact() {

        pmBO.rejectScenarioImpact("IMP001");

        verify(impactRepo).updateApprovalStatus("IMP001", false);
    }

    /* ============================
       ✅ NAV SERIES (NEW COVERAGE)
       ============================ */

    @Test
    public void testGetNavSeriesByScenario() {

        ScenarioImpactResult impact = new ScenarioImpactResult();
        impact.setImpactId("IMP001");

        when(impactRepo.findByScenarioIdOrderByImpactId("SC001"))
                .thenReturn(List.of(impact));

        when(navSeriesRepo.findByImpactId("IMP001"))
                .thenReturn(List.of(new ScenarioNavSeries()));

        assertFalse(pmBO.getNavSeriesByScenario("SC001").isEmpty());
    }

    /* ============================
       PROFILE
       ============================ */

    @Test
    public void testViewProfile_Success() {

        InvestorProfile profile = new InvestorProfile();
        profile.setInvestorId("PM001");

        when(profileRepo.getProfileByUserId("PM001"))
                .thenReturn(profile);

        assertEquals("PM001",
                pmBO.viewProfile("PM001").getUserId());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewProfile_NotFound() {

        when(profileRepo.getProfileByUserId("PM001"))
                .thenReturn(null);

        pmBO.viewProfile("PM001");
    }

    @Test
public void testUpdateProfile() {

    UserProfileDTO dto = new UserProfileDTO();
    dto.setUserId("PM001");
    dto.setFirstName("Rahul");
    dto.setLastName("Sharma");
    dto.setMobile("9000000002");
    dto.setCurrentAddress("Mumbai");
    dto.setPermanentAddress("Mumbai");
    dto.setPan("PMPAN001A");
    dto.setDob(java.time.LocalDate.of(1985, 6, 15));

    pmBO.updateProfile(dto);

    verify(profileRepo).updateProfile(
            eq("PM001"),
            eq("Rahul"),
            eq("Sharma"),
            eq("9000000002"),
            eq("Mumbai"),
            eq("Mumbai"),
            eq("PMPAN001A"),
            eq(java.time.LocalDate.of(1985, 6, 15))
    );
}
}