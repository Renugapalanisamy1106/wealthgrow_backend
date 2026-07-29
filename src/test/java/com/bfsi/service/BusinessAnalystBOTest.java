package com.bfsi.service;

import com.bfsi.entity.*;
import com.bfsi.exception.*;
import com.bfsi.repository.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BusinessAnalystBOTest {

    @Mock private ScenarioRepository scenarioRepo;
    @Mock private MutualFundRepository fundRepo;
    @Mock private ScenarioImpactResultRepository impactRepo;
    @Mock private DataEvaluationRepository dataEvaluationRepo;
    @Mock private UserProfileRepository profileRepo;
    @Mock private ScenarioNavSeriesRepository navSeriesRepo;

    @Mock private NotificationBO notificationBO;

    @InjectMocks
    private BusinessAnalystBO analystBO;

    @Before
    public void setup() {}

    /* ============================
       1. VIEW SCENARIOS
       ============================ */

    @Test
    public void testViewAllScenarios_Success() {

        when(scenarioRepo.findAll())
                .thenReturn(List.of(new ScenarioAnalysis()));

        List<ScenarioAnalysis> result =
                analystBO.viewAllScenarios();

        assertFalse(result.isEmpty());
    }

    @Test
    public void testViewAllScenarios_Empty() {

        when(scenarioRepo.findAll())
                .thenReturn(Collections.emptyList());

        List<ScenarioAnalysis> result =
                analystBO.viewAllScenarios();

        assertTrue(result.isEmpty());
    }

    /* ============================
       2. ACTIVE FUNDS
       ============================ */

    @Test
    public void testGetFundsForEvaluation_Success() {

        when(fundRepo.findByStatus("ACTIVE"))
                .thenReturn(List.of(new MutualFundProduct()));

        List<MutualFundProduct> result =
                analystBO.getFundsForEvaluation();

        assertEquals(1, result.size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testGetFundsForEvaluation_Empty() {

        when(fundRepo.findByStatus("ACTIVE"))
                .thenReturn(Collections.emptyList());

        analystBO.getFundsForEvaluation();
    }

    /* ============================
       3. SAVE IMPACT RESULT
       ============================ */

    @Test
    public void testSaveImpactResult() {

        analystBO.saveScenarioImpactResult(
                "SC001",
                "MF001",
                0.85,
                0.60,
                "HOLD",
                "Test Analysis"
        );

        verify(impactRepo).save(any(ScenarioImpactResult.class));
    }

    /* ============================
       4. SUBMIT FOR APPROVAL
       ============================ */

    @Test
    public void testSubmitScenarioAnalysis() {

        when(dataEvaluationRepo
                .findByScenarioIdAndEvaluatorRoleAndStatus("SC001","PM","PENDING"))
                .thenReturn(Optional.empty());

        DataEvaluation result = analystBO
                .submitScenarioAnalysisForApproval("SC001", "PM", "BA001");

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());

        verify(dataEvaluationRepo).save(any(DataEvaluation.class));
    }

    /* ============================
       5. VIEW IMPACT RESULTS
       ============================ */

    @Test
    public void testViewImpactResults_Success() {

        when(impactRepo.findByScenarioIdOrderByImpactId("SC001"))
                .thenReturn(List.of(new ScenarioImpactResult()));

        List<ScenarioImpactResult> result =
                analystBO.viewScenarioImpactResults("SC001");

        assertFalse(result.isEmpty());
    }

    @Test
    public void testViewImpactResults_Empty() {

        when(impactRepo.findByScenarioIdOrderByImpactId("SC001"))
                .thenReturn(Collections.emptyList());

        List<ScenarioImpactResult> result =
                analystBO.viewScenarioImpactResults("SC001");

        assertTrue(result.isEmpty());
    }

    /* ============================
       6. NAV SERIES ✅ FIXED
       ============================ */

    @Test
    public void testGetNavSeries_Success() {

        when(navSeriesRepo
                .findByImpactIdOrderBySequenceNo("IMP001"))
                .thenReturn(List.of(new ScenarioNavSeries()));

        List<ScenarioNavSeries> list =
                analystBO.getNavSeries("IMP001");

        assertFalse(list.isEmpty());
    }

    @Test
    public void testGetNavSeries_Empty() {

        when(navSeriesRepo
                .findByImpactIdOrderBySequenceNo("IMP001"))
                .thenReturn(Collections.emptyList());

        List<ScenarioNavSeries> list =
                analystBO.getNavSeries("IMP001");

        assertTrue(list.isEmpty());
    }

    /* ============================
       ADDITIONAL COVERAGE
       ============================ */

    @Test
    public void testGetPendingApprovals() {

        when(impactRepo.findByApprovedFalse())
                .thenReturn(List.of(new ScenarioImpactResult()));

        assertFalse(analystBO.getPendingApprovals().isEmpty());
    }

    @Test
    public void testGetAllEvaluations() {

        when(dataEvaluationRepo.findBySubmittedByOrderByCreatedAtDesc("BA001"))
                .thenReturn(List.of(new DataEvaluation()));

        assertFalse(analystBO.getAllEvaluations("BA001").isEmpty());
    }

    @Test
    public void testGetNotifications() {

        when(dataEvaluationRepo.findBySubmittedByOrderByCreatedAtDesc("BA001"))
                .thenReturn(List.of(new DataEvaluation()));

        assertFalse(analystBO.getNotifications("BA001").isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewProfile_NotFound() {

        when(profileRepo.getProfileByUserId("U1"))
                .thenReturn(null);

        analystBO.viewProfile("U1");
    }
}
