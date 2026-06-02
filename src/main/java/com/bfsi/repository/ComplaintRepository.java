package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.Complaint;
import com.bfsi.entity.ComplaintDetails;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, String> {

    /* ============================
       FETCH COMPLAINTS
       ============================ */

    List<Complaint> findAll();

    List<Complaint> findByInvestorId(String investorId);

    List<Complaint> findByStatus(String status);

    Complaint findByComplaintId(String complaintId);


    /* ============================
       COMPLAINT DETAILS
       ============================ */

    @Query("""
        SELECT d FROM ComplaintDetails d
        WHERE d.complaintId = :complaintId
    """)
    ComplaintDetails getComplaintDetails(
            @Param("complaintId") String complaintId
    );


    /* ============================
       RAISE COMPLAINT
       ============================ */

    // ✅ handled using save() in BO


    /* ============================
       RESOLVE COMPLAINT
       ============================ */

    @Modifying
    @Transactional
    @Query("""
        UPDATE Complaint c
        SET c.status = 'RESOLVED'
        WHERE c.complaintId = :complaintId
    """)
    void resolveComplaint(
            @Param("complaintId") String complaintId
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE ComplaintDetails d
        SET d.resolution = :resolution
        WHERE d.complaintId = :complaintId
    """)
    void updateResolution(
            @Param("complaintId") String complaintId,
            @Param("resolution") String resolution
    );


    /* ============================
       DELETE COMPLAINT
       ============================ */

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Complaint c
        WHERE c.complaintId = :complaintId
    """)
    void deleteComplaint(
            @Param("complaintId") String complaintId
    );


    /* ============================
       UPDATE COMPLAINT
       ============================ */

    @Modifying
    @Transactional
    @Query("""
        UPDATE Complaint c
        SET c.category = :category
        WHERE c.complaintId = :complaintId
    """)
    void updateComplaintCategory(
            @Param("complaintId") String complaintId,
            @Param("category") String category
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE ComplaintDetails d
        SET d.description = :description
        WHERE d.complaintId = :complaintId
    """)
    void updateComplaintDescription(
            @Param("complaintId") String complaintId,
            @Param("description") String description
    );


    /* ============================
       SUPPORT
       ============================ */

    @Query("""
        SELECT c.investorId
        FROM Complaint c
        WHERE c.complaintId = :complaintId
    """)
    String getInvestorIdByComplaint(
            @Param("complaintId") String complaintId
    );
}
