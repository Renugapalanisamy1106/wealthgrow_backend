package com.bfsi.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Collections;

import com.bfsi.dto.ComplaintDTO;
import com.bfsi.dto.RaiseComplaintDTO;
import com.bfsi.entity.Complaint;
import com.bfsi.entity.ComplaintDetails;
import com.bfsi.service.ComplaintManagerBO;
import com.bfsi.dto.UserProfileDTO;

@RestController
@RequestMapping("/complaints")
@CrossOrigin(origins = {
    "http://localhost:4200",
    "https://wealthgrow-frontend.vercel.app/"
})  // ✅ ADDED — was missing
public class ComplaintManagerController {

    private final ComplaintManagerBO complaintBO;

    public ComplaintManagerController(ComplaintManagerBO complaintBO) {
        this.complaintBO = complaintBO;
    }

    /* ============================
       VIEW COMPLAINTS
       ============================ */

    @GetMapping(produces = "application/json")
    public List<Complaint> getAllComplaints() {
        try {
            return complaintBO.viewAllComplaints();
        } catch (Exception e) {
            // ✅ Return empty list — not 500 error — when no complaints exist
            return Collections.emptyList();
        }
    }

    @GetMapping(value = "/investor/{investorId}", produces = "application/json")
    public List<Complaint> getComplaintsByInvestor(@PathVariable String investorId) {
        try {
            return complaintBO.viewComplaintsByInvestor(investorId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @GetMapping(value = "/status/{status}", produces = "application/json")
    public List<Complaint> getComplaintsByStatus(@PathVariable String status) {
        try {
            return complaintBO.viewComplaintsByStatus(status);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /* ============================
       COMPLAINT DETAILS
       ============================ */

    @GetMapping(value = "/{complaintId}", produces = "application/json")
    public java.util.Map<String, Object> getComplaintDetails(@PathVariable String complaintId) {
        return complaintBO.viewComplaintDetailsFull(complaintId);
    }

    /* ============================
       RESOLUTION — auto-sends notification to investor
       ============================ */

    @PutMapping(value = "/{complaintId}/resolve", consumes = "text/plain")
    public String resolveComplaint(
            @PathVariable String complaintId,
            @RequestBody String resolutionRemarks) {

        complaintBO.resolveComplaint(complaintId, resolutionRemarks);
        return "Complaint resolved and investor notified";
    }

    /* ============================
       UPDATE / DELETE
       ============================ */

    @PutMapping(value = "/{complaintId}", consumes = "application/json")
    public String updateComplaint(
            @PathVariable String complaintId,
            @RequestBody RaiseComplaintDTO dto) {
        complaintBO.updateComplaint(complaintId, dto);
        return "Complaint updated successfully";
    }

    @DeleteMapping("/{complaintId}")
    public String deleteComplaint(@PathVariable String complaintId) {
        complaintBO.deleteComplaint(complaintId);
        return "Complaint deleted successfully";
    }

    /* ============================
       MANUAL NOTIFICATION
       ============================ */

    @PostMapping(value = "/notify", consumes = "application/json")
    public String sendNotification(@RequestBody ComplaintDTO dto) {
        complaintBO.sendNotification(dto);
        return "Notification sent successfully";
    }

    /* ============================
       PROFILE
       ============================ */

    @GetMapping(value = "/profile/{userId}", produces = "application/json")
    public UserProfileDTO viewProfile(@PathVariable String userId) {
        return complaintBO.viewProfile(userId);
    }

    @PutMapping(value = "/profile", consumes = "application/json")
    public String updateProfile(@RequestBody UserProfileDTO dto) {
        complaintBO.updateProfile(dto);
        return "Profile updated successfully";
    }
}
