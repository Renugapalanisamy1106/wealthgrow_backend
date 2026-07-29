package com.bfsi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bfsi.entity.Notification;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.NotificationRepository;
import com.bfsi.repository.UserRepository;

@Service
public class NotificationBO {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    public NotificationBO(NotificationRepository notificationRepo,
                          UserRepository userRepo) {
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
    }

    /* ============================
       CREATE NOTIFICATION (single user)
       Central helper so every flow creates notifications consistently.
       ============================ */
    public void createNotification(String userId, String type, String message) {
        if (userId == null || userId.isBlank()) {
            return; // no recipient — nothing to do
        }
        Notification n = new Notification();
        n.setNotificationId(UUID.randomUUID().toString());
        n.setUserId(userId);
        n.setType(type);
        n.setMessage(message);
        n.setStatus("UNREAD");
        n.setCreatedAt(LocalDateTime.now());
        notificationRepo.save(n);
    }

    /* ============================
       CREATE NOTIFICATION FOR EVERY USER OF A ROLE
       e.g. notify all ADMIN / PORTFOLIO_MANAGER / OPERATIONS users.
       ============================ */
    public void createNotificationForRole(String roleId, String type, String message) {
        List<String> userIds = userRepo.findUserIdsByRole(roleId);
        if (userIds == null) {
            return;
        }
        for (String uid : userIds) {
            createNotification(uid, type, message);
        }
    }

    /* ============================
       FETCH USER NOTIFICATIONS
       ============================ */

    public List<Notification> getUserNotifications(String userId) {

        List<Notification> notifications =
                notificationRepo.findByUserId(userId);

        if (notifications == null || notifications.isEmpty()) {
            throw new DataNotFoundException(
                    "No notifications found for user."
            );
        }

        return notifications;
    }

    /* ============================
       FETCH UNREAD NOTIFICATIONS
       ============================ */

    public List<Notification> getUnreadNotifications(String userId) {

        List<Notification> notifications =
                notificationRepo.findByUserIdAndStatusNot(userId, "READ");

        if (notifications == null || notifications.isEmpty()) {
            throw new DataNotFoundException(
                    "No unread notifications found for user."
            );
        }

        return notifications;
    }

    /* ============================
       MARK SINGLE AS READ
       ============================ */

    public void markNotificationAsRead(String notificationId) {
        notificationRepo.markAsRead(notificationId);
    }

    /* ============================
       MARK ALL AS READ
       ============================ */

    public void markAllNotificationsAsRead(String userId) {
        notificationRepo.markAllAsRead(userId);
    }
}