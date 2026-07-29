package com.bfsi.service;

import com.bfsi.entity.Notification;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.NotificationRepository;
import com.bfsi.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;

import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationBOTest {

    @Mock
    private NotificationRepository notificationRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private NotificationBO notificationBO;

    /* ============================
       GET USER NOTIFICATIONS
       ============================ */

    @Test
    public void testGetUserNotifications_Success() {

        when(notificationRepo.findByUserId("INV001"))
                .thenReturn(Collections.singletonList(new Notification()));

        List<Notification> result =
                notificationBO.getUserNotifications("INV001");

        assertEquals(1, result.size());
    }

    @Test(expected = DataNotFoundException.class)
    public void testGetUserNotifications_Empty() {

        when(notificationRepo.findByUserId("INV001"))
                .thenReturn(Collections.emptyList());

        notificationBO.getUserNotifications("INV001");
    }

    /* ============================
       GET UNREAD NOTIFICATIONS
       ============================ */

    @Test
    public void testGetUnreadNotifications_Success() {

        when(notificationRepo.findByUserIdAndStatusNot("INV001", "READ"))
                .thenReturn(Collections.singletonList(new Notification()));

        List<Notification> result =
                notificationBO.getUnreadNotifications("INV001");

        assertFalse(result.isEmpty());
    }

    @Test(expected = DataNotFoundException.class)
    public void testGetUnreadNotifications_Empty() {

        when(notificationRepo.findByUserIdAndStatusNot("INV001", "READ"))
                .thenReturn(Collections.emptyList());

        notificationBO.getUnreadNotifications("INV001");
    }

    /* ============================
       MARK SINGLE AS READ
       ============================ */

    @Test
    public void testMarkNotificationAsRead() {

        notificationBO.markNotificationAsRead("NOTIF001");

        verify(notificationRepo).markAsRead("NOTIF001");
    }

    /* ============================
       MARK ALL AS READ
       ============================ */

    @Test
    public void testMarkAllNotificationsAsRead() {

        notificationBO.markAllNotificationsAsRead("INV001");

        verify(notificationRepo).markAllAsRead("INV001");
    }

    /* ============================
       ✅ NEW: CREATE NOTIFICATION (single user)
       ============================ */
    @Test
    public void testCreateNotification_Saves() {

        notificationBO.createNotification("INV001", "TEST_TYPE", "hello");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepo).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals("INV001", saved.getUserId());
        assertEquals("TEST_TYPE", saved.getType());
        assertEquals("hello", saved.getMessage());
        assertEquals("UNREAD", saved.getStatus());
        assertNotNull(saved.getNotificationId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    public void testCreateNotification_SkipsNullUser() {

        notificationBO.createNotification(null, "TEST_TYPE", "hello");
        notificationBO.createNotification("", "TEST_TYPE", "hello");

        verify(notificationRepo, never()).save(any());
    }

    /* ============================
       ✅ NEW: CREATE NOTIFICATION FOR ROLE
       ============================ */
    @Test
    public void testCreateNotificationForRole_NotifiesEachUser() {

        when(userRepo.findUserIdsByRole("OPERATIONS"))
                .thenReturn(List.of("OPS001", "OPS002"));

        notificationBO.createNotificationForRole("OPERATIONS", "ALERT", "msg");

        // one save per user in the role
        verify(notificationRepo, times(2)).save(any(Notification.class));
    }

    @Test
    public void testCreateNotificationForRole_EmptyRole() {

        when(userRepo.findUserIdsByRole("OPERATIONS"))
                .thenReturn(Collections.emptyList());

        notificationBO.createNotificationForRole("OPERATIONS", "ALERT", "msg");

        verify(notificationRepo, never()).save(any());
    }
}