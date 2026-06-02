package com.bfsi.service;

import com.bfsi.dto.*;
import com.bfsi.entity.*;
import com.bfsi.exception.*;
import com.bfsi.repository.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminBOTest {

    @Mock private MutualFundRepository fundRepo;
    @Mock private ScenarioRepository scenarioRepo;
    @Mock private DataEvaluationRepository dataEvaluationRepo;
    @Mock private UserRepository userRepo;
    @Mock private UserProfileRepository profileRepo;
    @Mock private AdminRepository adminRepo;

    @InjectMocks
    private AdminBO adminBO;

    @Before
    public void setup() throws Exception {
        Field field = AdminBO.class.getDeclaredField("adminRepo");
        field.setAccessible(true);
        field.set(adminBO, adminRepo);
    }

    /* ============================
       DASHBOARD
       ============================ */

    @Test
    public void testDashboard() {
        when(adminRepo.getTotalFunds()).thenReturn(1);
        when(adminRepo.getActiveUsers()).thenReturn(5);
        when(adminRepo.getPendingRequests()).thenReturn(2);
        when(adminRepo.getPendingComplaints()).thenReturn(3);
        when(adminRepo.getResolvedComplaints()).thenReturn(1); // ✅ FIX

        AdminDashboardDTO dto = adminBO.getDashboard();

        assertEquals(1, dto.getTotalFunds());
        assertEquals(5, dto.getActiveUsers());
        assertEquals(2, dto.getPendingRequests());
        assertEquals(3, dto.getPendingComplaints());
        assertEquals(1, dto.getResolvedComplaints()); // ✅ NEW ASSERT
    }

    /* ============================
       ADD FUND
       ============================ */

    @Test
    public void testAddMutualFund() {
        AddMutualFundDTO dto = new AddMutualFundDTO();
        dto.setFundId("MF1");

        adminBO.addMutualFund(dto);

        verify(fundRepo).save(any());
    }

    /* ============================
       GET FUNDS
       ============================ */

    @Test
    public void testGetFundsEmpty() {
        when(fundRepo.findAll()).thenReturn(Collections.emptyList());
        assertTrue(adminBO.getAllMutualFunds().isEmpty());
    }

    @Test
    public void testGetFundsSuccess() {
        when(fundRepo.findAll()).thenReturn(List.of(new MutualFundProduct()));
        assertFalse(adminBO.getAllMutualFunds().isEmpty());
    }

    /* ============================
       UPDATE FUND
       ============================ */

    @Test
    public void testUpdateFundSuccess() {
        MutualFundProduct fund = new MutualFundProduct();

        when(fundRepo.findById("MF1")).thenReturn(Optional.of(fund));

        UpdateMutualFundDTO dto = new UpdateMutualFundDTO();
        dto.setFundId("MF1");

        adminBO.updateMutualFund(dto);

        verify(fundRepo).save(fund);
    }

    @Test(expected = DataNotFoundException.class)
    public void testUpdateFundNotFound() {
        when(fundRepo.findById("X")).thenReturn(Optional.empty());

        UpdateMutualFundDTO dto = new UpdateMutualFundDTO();
        dto.setFundId("X");

        adminBO.updateMutualFund(dto);
    }

    /* ============================
       DELETE FUND
       ============================ */

    @Test
    public void testDeleteFund() {
        adminBO.deleteMutualFund("MF1");
        verify(fundRepo).deleteById("MF1");
    }

    /* ============================
       PROMOTE / DEMOTE
       ============================ */

    @Test
    public void testPromoteFund() {
        MutualFundProduct fund = new MutualFundProduct();

        when(fundRepo.findById("MF1")).thenReturn(Optional.of(fund));

        adminBO.promoteFund("MF1");

        assertEquals("PROMOTED", fund.getPromotionStatus());
    }

    @Test
    public void testDemoteFund() {
        MutualFundProduct fund = new MutualFundProduct();

        when(fundRepo.findById("MF1")).thenReturn(Optional.of(fund));

        adminBO.demoteFund("MF1");

        assertEquals("NORMAL", fund.getPromotionStatus());
    }

    /* ============================
       SCENARIO
       ============================ */

    @Test
    public void testAddScenario() {
        ScenarioDTO dto = new ScenarioDTO();
        dto.setScenarioId("SC1");

        adminBO.addScenario(dto);

        verify(scenarioRepo).save(any());
    }

    @Test
    public void testUpdateScenarioSuccess() {
        ScenarioAnalysis sc = new ScenarioAnalysis();

        when(scenarioRepo.findById("SC1")).thenReturn(Optional.of(sc));

        ScenarioDTO dto = new ScenarioDTO();
        dto.setScenarioId("SC1");

        adminBO.updateScenario(dto);

        verify(scenarioRepo).save(sc);
    }

    @Test(expected = DataNotFoundException.class)
    public void testUpdateScenarioNotFound() {
        when(scenarioRepo.findById("SCX")).thenReturn(Optional.empty());

        ScenarioDTO dto = new ScenarioDTO();
        dto.setScenarioId("SCX");

        adminBO.updateScenario(dto);
    }

    @Test
    public void testDeleteScenario() {
        adminBO.deleteScenario("SC1");
        verify(scenarioRepo).deleteScenario("SC1");
    }

    /* ============================
       ✅ FIXED: SCENARIOS (NO EXCEPTION)
       ============================ */

    @Test
    public void testGetAllScenariosSuccess() {
        when(scenarioRepo.findAll()).thenReturn(List.of(new ScenarioAnalysis()));

        assertFalse(adminBO.getAllScenarios().isEmpty());
    }

    @Test
    public void testGetAllScenariosEmpty() {

        when(scenarioRepo.findAll()).thenReturn(Collections.emptyList());

        List<ScenarioAnalysis> list = adminBO.getAllScenarios();

        assertTrue(list.isEmpty());
    }

    /* ============================
       ✅ FIXED: EVALUATION
       ============================ */

    @Test
    public void testViewEvaluationsSuccess() {

        when(dataEvaluationRepo
                .findByStatusAndEvaluatorRole("PENDING", "ADMIN"))
                .thenReturn(List.of(new DataEvaluation()));

        assertFalse(adminBO.viewPendingEvaluations().isEmpty());
    }

    @Test
    public void testViewEvaluationsEmpty() {

        when(dataEvaluationRepo
                .findByStatusAndEvaluatorRole("PENDING", "ADMIN"))
                .thenReturn(Collections.emptyList());

        assertTrue(adminBO.viewPendingEvaluations().isEmpty());
    }

    @Test
    public void testApproveEvaluation() {
        adminBO.approveEvaluation("E1");

        verify(dataEvaluationRepo).updateEvaluationStatus("E1", "APPROVED");
    }

    @Test
    public void testRejectEvaluation() {
        adminBO.rejectEvaluation("E1");

        verify(dataEvaluationRepo).updateEvaluationStatus("E1", "REJECTED");
    }

    /* ============================
       PROFILE
       ============================ */

    @Test
    public void testProfileSuccess() {
        InvestorProfile p = new InvestorProfile();
        p.setInvestorId("A1");

        when(profileRepo.getProfileByUserId("A1")).thenReturn(p);

        UserProfileDTO dto = adminBO.viewProfile("A1");

        assertEquals("A1", dto.getUserId());
    }

    @Test(expected = DataNotFoundException.class)
    public void testProfileNotFound() {
        when(profileRepo.getProfileByUserId("A1")).thenReturn(null);

        adminBO.viewProfile("A1");
    }

    @Test
    public void testUpdateProfileSuccess() {

        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId("A1");

        adminBO.updateProfile(dto);

        verify(profileRepo).updateProfile(
                eq("A1"),
                nullable(String.class),
                nullable(String.class),
                nullable(String.class),
                nullable(String.class),
                nullable(String.class),
                nullable(LocalDate.class)
        );
    }

    @Test(expected = DataNotFoundException.class)
    public void testUpdateProfileInvalidId() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId("");

        adminBO.updateProfile(dto);
    }
}