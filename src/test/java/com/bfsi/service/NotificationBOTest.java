package com.bfsi.service;

import com.bfsi.entity.Notification;
import com.bfsi.exception.DataNotFoundException;
import com.bfsi.repository.NotificationRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;

import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationBOTest {

    @Mock
    private NotificationRepository notificationRepo;

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
}