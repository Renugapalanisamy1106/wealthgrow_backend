package com.bfsi.service;

import com.bfsi.dto.*;
import com.bfsi.entity.*;
import com.bfsi.exception.*;
import com.bfsi.repository.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;

import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ComplaintManagerBOTest {

    @Mock private ComplaintRepository complaintRepo;
    @Mock private NotificationRepository notificationRepo;

    @InjectMocks
    private ComplaintManagerBO complaintBO;

    /* ============================
       VIEW COMPLAINTS
       ============================ */

    @Test
    public void testViewAllComplaints_Success() {

        when(complaintRepo.findAll())
                .thenReturn(List.of(new Complaint()));

        List<Complaint> result = complaintBO.viewAllComplaints();

        assertEquals(1, result.size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewAllComplaints_Empty() {

        when(complaintRepo.findAll())
                .thenReturn(Collections.emptyList());

        complaintBO.viewAllComplaints();
    }

    @Test
    public void testViewComplaintsByInvestor() {

        when(complaintRepo.findByInvestorId("INV001"))
                .thenReturn(List.of(new Complaint()));

        List<Complaint> result =
                complaintBO.viewComplaintsByInvestor("INV001");

        assertFalse(result.isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewComplaintsByInvestor_Empty() {

        when(complaintRepo.findByInvestorId("INV001"))
                .thenReturn(Collections.emptyList());

        complaintBO.viewComplaintsByInvestor("INV001");
    }

    /* ============================
       BY STATUS
       ============================ */

    @Test
    public void testViewComplaintsByStatus() {

        when(complaintRepo.findByStatus("OPEN"))
                .thenReturn(List.of(new Complaint()));

        List<Complaint> result =
                complaintBO.viewComplaintsByStatus("OPEN");

        assertEquals(1, result.size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewComplaintsByStatus_Empty() {

        when(complaintRepo.findByStatus("OPEN"))
                .thenReturn(Collections.emptyList());

        complaintBO.viewComplaintsByStatus("OPEN");
    }

    /* ============================
       DETAILS
       ============================ */

    @Test
    public void testViewComplaintDetails() {

        ComplaintDetails details = new ComplaintDetails();

        when(complaintRepo.getComplaintDetails("CMP001"))
                .thenReturn(details);

        ComplaintDetails result =
                complaintBO.viewComplaintDetails("CMP001");

        assertNotNull(result);
    }

    @Test(expected = DataNotFoundException.class)
    public void testViewComplaintDetails_NotFound() {

        when(complaintRepo.getComplaintDetails("CMP001"))
                .thenReturn(null);

        complaintBO.viewComplaintDetails("CMP001");
    }

    /* ============================
       RESOLVE COMPLAINT
       ============================ */

    @Test
    public void testResolveComplaint_Success() {

        when(complaintRepo.getInvestorIdByComplaint("CMP001"))
                .thenReturn("INV001");

        complaintBO.resolveComplaint("CMP001", "Issue fixed");

        verify(complaintRepo).resolveComplaint("CMP001");
        verify(complaintRepo).updateResolution("CMP001", "Issue fixed");
        verify(notificationRepo).save(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveComplaint_InvalidRemarks() {

        complaintBO.resolveComplaint("CMP001", " ");
    }

    /* ============================
       UPDATE COMPLAINT
       ============================ */

    @Test
    public void testUpdateComplaint_Success() {

        RaiseComplaintDTO dto = new RaiseComplaintDTO();
        dto.setCategory("Category");
        dto.setDescription("Desc");

        when(complaintRepo.findByComplaintId("CMP001"))
                .thenReturn(new Complaint());

        complaintBO.updateComplaint("CMP001", dto);

        verify(complaintRepo)
                .updateComplaintCategory("CMP001", "Category");

        verify(complaintRepo)
                .updateComplaintDescription("CMP001", "Desc");
    }

    @Test(expected = DataNotFoundException.class)
    public void testUpdateComplaint_NotFound() {

        when(complaintRepo.findByComplaintId("CMP001"))
                .thenReturn(null);

        complaintBO.updateComplaint("CMP001", new RaiseComplaintDTO());
    }

    /* ============================
       DELETE COMPLAINT
       ============================ */

    @Test
    public void testDeleteComplaint() {

        when(complaintRepo.findByComplaintId("CMP001"))
                .thenReturn(new Complaint());

        complaintBO.deleteComplaint("CMP001");

        verify(complaintRepo).deleteComplaint("CMP001");
    }

    @Test(expected = DataNotFoundException.class)
    public void testDeleteComplaint_NotFound() {

        when(complaintRepo.findByComplaintId("CMP001"))
                .thenReturn(null);

        complaintBO.deleteComplaint("CMP001");
    }

    /* ============================
       MANUAL NOTIFICATION
       ============================ */

    @Test
    public void testSendNotification() {

        ComplaintDTO dto = new ComplaintDTO();
        dto.setInvestorId("INV001");
        dto.setType("ALERT");
        dto.setMessage("Test message");

        complaintBO.sendNotification(dto);

        verify(notificationRepo, times(1))
                .save(any());
    }
}