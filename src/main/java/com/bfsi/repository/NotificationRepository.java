package com.bfsi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bfsi.entity.Notification;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, String> {

    /* ================================
       FETCH ALL NOTIFICATIONS
       ================================ */
    List<Notification> findAll();


    /* ================================
       FETCH BY USER
       ================================ */
    List<Notification> findByUserId(String userId);


    /* ================================
       FETCH UNREAD NOTIFICATIONS
       ================================ */
    List<Notification> findByUserIdAndStatusNot(
            String userId,
            String status
    );


    /* ================================
       ✅ ✅ FETCH OPERATIONS NOTIFICATIONS (NEW 🔥)
       ================================ */
    @Query("""
        SELECT n FROM Notification n
        WHERE n.type IN ('NAV_ASSIGN', 'ALERT')
        ORDER BY n.createdAt DESC
    """)
    List<Notification> getOperationNotifications();


    /* ================================
       MARK SINGLE AS READ
       ================================ */
    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n
        SET n.status = 'READ'
        WHERE n.notificationId = :notificationId
    """)
    void markAsRead(
            @Param("notificationId") String notificationId
    );


    /* ================================
       CREATE NOTIFICATION
       ================================ */
    // ✅ handled by save()


    /* ================================
       MARK ALL AS READ
       ================================ */
    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n
        SET n.status = 'READ'
        WHERE n.userId = :userId AND n.status <> 'READ'
    """)
    void markAllAsRead(
            @Param("userId") String userId
    );
}