package com.bfsi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bfsi.service.NotificationBO;
import com.bfsi.entity.Notification;

/**
 * REST Controller for Notification APIs
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationBO notificationBO;

    public NotificationController(NotificationBO notificationBO) {
        this.notificationBO = notificationBO;
    }

    /* ============================
       FETCH ALL USER NOTIFICATIONS
       ============================ */

    @GetMapping(value = "/{userId}", produces = "application/json")
    public List<Notification> getUserNotifications(
            @PathVariable String userId) {

        return notificationBO.getUserNotifications(userId);
    }

    /* ============================
       FETCH UNREAD NOTIFICATIONS
       ============================ */

    @GetMapping(value = "/{userId}/unread", produces = "application/json")
    public List<Notification> getUnreadNotifications(
            @PathVariable String userId) {

        return notificationBO.getUnreadNotifications(userId);
    }

    /* ============================
       MARK SINGLE NOTIFICATION AS READ
       ============================ */

    @PutMapping("/{notificationId}/read")
    public String markNotificationAsRead(
            @PathVariable String notificationId) {

        notificationBO.markNotificationAsRead(notificationId);
        return "Notification marked as read successfully";
    }

    /* ============================
       MARK ALL NOTIFICATIONS AS READ
       ============================ */

    @PutMapping("/{userId}/read-all")
    public String markAllNotificationsAsRead(
            @PathVariable String userId) {

        notificationBO.markAllNotificationsAsRead(userId);
        return "All notifications marked as read successfully";
    }
}