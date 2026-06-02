package com.bfsi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * JPA Entity for notification table
 */
@Entity
@Table(name = "notification", schema = "bfsimf_clean")
public class Notification {

    @Id
    @Column(name = "notification_id")
    private String notificationId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "message")
    private String message;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(String notificationId, String userId,
                        String message, String type,
                        String status, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}