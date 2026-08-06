package com.retailflow.notificationservice.notification.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default implementation that logs the notification.
 * Replace with a real SMTP/SMS integration later without touching business logic.
 */
@Slf4j
@Service
public class ConsoleNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("Sending notification to [{}]: subject='{}', body='{}'", recipient, subject, body);
    }
}
