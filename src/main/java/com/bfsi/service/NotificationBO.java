package com.bfsi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bfsi.entity.Notification;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.NotificationRepository;

@Service
public class NotificationBO {

    private final NotificationRepository notificationRepo;

    public NotificationBO(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
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