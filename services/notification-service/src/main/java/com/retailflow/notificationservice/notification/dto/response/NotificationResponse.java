package com.retailflow.notificationservice.notification.dto.response;

import com.retailflow.notificationservice.notification.entity.NotificationChannel;
import com.retailflow.notificationservice.notification.entity.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private Long id;
    private String recipient;
    private NotificationChannel channel;
    private String subject;
    private String body;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
