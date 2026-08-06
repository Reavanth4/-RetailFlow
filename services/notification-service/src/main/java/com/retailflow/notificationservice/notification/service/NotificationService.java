package com.retailflow.notificationservice.notification.service;

import com.retailflow.notificationservice.notification.dto.request.NotificationCreateRequest;
import com.retailflow.notificationservice.notification.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createAndSend(NotificationCreateRequest request);

    NotificationResponse getNotificationById(Long id);

    List<NotificationResponse> getAllNotifications();

    List<NotificationResponse> getNotificationsByRecipient(String recipient);
}
