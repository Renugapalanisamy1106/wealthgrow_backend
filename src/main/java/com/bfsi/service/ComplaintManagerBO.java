package com.bfsi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bfsi.dto.ComplaintDTO;
import com.bfsi.dto.RaiseComplaintDTO;
import com.bfsi.entity.Complaint;
import com.bfsi.entity.ComplaintDetails;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.ComplaintRepository;
import com.bfsi.repository.NotificationRepository;
import com.bfsi.repository.UserProfileRepository;
import com.bfsi.dto.UserProfileDTO;

@Service
public class ComplaintManagerBO {

    private final ComplaintRepository complaintRepo;
    private final NotificationRepository notificationRepo;
    private final UserProfileRepository profileRepo;

    public ComplaintManagerBO(
        ComplaintRepository complaintRepo,
        NotificationRepository notificationRepo,
        UserProfileRepository profileRepo) {

    this.complaintRepo = complaintRepo;
    this.notificationRepo = notificationRepo;
    this.profileRepo = profileRepo;
}

    /* ============================
       VIEW COMPLAINTS
       ============================ */

    public List<Complaint> viewAllComplaints() {

        List<Complaint> complaints = complaintRepo.findAll();

        if (complaints.isEmpty()) {
            throw new DataNotFoundException("No complaints found.");
        }
        return complaints;
    }

    public List<Complaint> viewComplaintsByInvestor(String investorId) {

        List<Complaint> complaints =
                complaintRepo.findByInvestorId(investorId);

        if (complaints.isEmpty()) {
            throw new DataNotFoundException(
                    "No complaints found for investor."
            );
        }
        return complaints;
    }

    public List<Complaint> viewComplaintsByStatus(String status) {

        List<Complaint> complaints =
                complaintRepo.findByStatus(status);

        if (complaints.isEmpty()) {
            throw new DataNotFoundException(
                    "No complaints found with status: " + status
            );
        }
        return complaints;
    }

    /* ============================
       COMPLAINT DETAILS
       ============================ */

    public ComplaintDetails viewComplaintDetails(String complaintId) {

        ComplaintDetails details =
                complaintRepo.getComplaintDetails(complaintId);

        if (details == null) {
            throw new DataNotFoundException(
                    "Complaint details not found."
            );
        }
        return details;
    }

    /* ============================
       COMPLAINT DETAILS (FULL)
       ============================ */

    // ✅ NEW — returns combined complaint + details for frontend complaint-details page
    public java.util.Map<String, Object> viewComplaintDetailsFull(String complaintId) {

        Complaint complaint = complaintRepo.findByComplaintId(complaintId);
        if (complaint == null) {
            throw new com.bfsi.exception.DataNotFoundException("Complaint not found: " + complaintId);
        }

        ComplaintDetails details = complaintRepo.getComplaintDetails(complaintId);

        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("complaintId",  complaint.getComplaintId());
        result.put("investorId",   complaint.getInvestorId());
        result.put("category",     complaint.getCategory());
        result.put("status",       complaint.getStatus());
        result.put("raisedDate",   complaint.getRaisedDate() != null ? complaint.getRaisedDate().toString() : null);
        result.put("priority",     complaint.getPriority());
        result.put("description",  details != null ? details.getDescription() : null);
        result.put("resolution",   details != null ? details.getResolution()  : null);
        return result;
    }

    /* ============================
       RESOLVE COMPLAINT
       ============================ */

    public void resolveComplaint(String complaintId,
                                 String resolutionRemarks) {

        if (resolutionRemarks == null ||
            resolutionRemarks.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Resolution remarks cannot be empty."
            );
        }

        complaintRepo.resolveComplaint(complaintId);
        complaintRepo.updateResolution(complaintId, resolutionRemarks);

        String investorId =
                complaintRepo.getInvestorIdByComplaint(complaintId);

        var notification = new com.bfsi.entity.Notification();
        notification.setNotificationId(java.util.UUID.randomUUID().toString());
        notification.setUserId(investorId);
        notification.setType("COMPLAINT_RESOLVED");
        notification.setMessage(
                "Your complaint has been resolved: " + resolutionRemarks
        );
        notification.setStatus("UNREAD");
        notification.setCreatedAt(java.time.LocalDateTime.now());

        notificationRepo.save(notification);
    }

    /* ============================
       UPDATE / DELETE COMPLAINT
       ============================ */

    public void updateComplaint(String complaintId,
                                RaiseComplaintDTO dto) {

        Complaint complaint =
                complaintRepo.findByComplaintId(complaintId);

        if (complaint == null) {
            throw new DataNotFoundException("Complaint not found.");
        }

        complaintRepo.updateComplaintCategory(
                complaintId,
                dto.getCategory()
        );

        complaintRepo.updateComplaintDescription(
                complaintId,
                dto.getDescription()
        );
    }

    public void deleteComplaint(String complaintId) {

        Complaint complaint =
                complaintRepo.findByComplaintId(complaintId);

        if (complaint == null) {
            throw new DataNotFoundException("Complaint not found.");
        }

        complaintRepo.deleteComplaint(complaintId);
    }

    /* ============================
       MANUAL NOTIFICATION
       ============================ */

    public void sendNotification(ComplaintDTO dto) {

        var notification = new com.bfsi.entity.Notification();
        notification.setNotificationId(java.util.UUID.randomUUID().toString());
        notification.setUserId(dto.getInvestorId());
        notification.setType(dto.getType());
        notification.setMessage(dto.getMessage());
        notification.setStatus("UNREAD");
        notification.setCreatedAt(java.time.LocalDateTime.now());

        notificationRepo.save(notification);
    }
    /* ============================
   PROFILE (COMPLAINT MANAGER)
   ============================ */

public UserProfileDTO viewProfile(String userId) {

    var entity = profileRepo.getProfileByUserId(userId);

    if (entity == null) {
        throw new DataNotFoundException("Profile not found");
    }

    UserProfileDTO dto = new UserProfileDTO();
    dto.setUserId(entity.getInvestorId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setEmail(entity.getEmail());
    dto.setMobile(entity.getMobile());
    dto.setDob(entity.getDob());
    dto.setPan(entity.getPan());
    dto.setAddress(entity.getAddress());

    return dto;
}

public void updateProfile(UserProfileDTO dto) {

    if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
        throw new DataNotFoundException("Invalid User ID");
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
}
