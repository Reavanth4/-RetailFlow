package com.retailflow.notificationservice.notification.sender;

/**
 * Abstraction over the actual notification delivery mechanism
 * (SMTP email, SMS provider, push service). Business logic should
 * depend only on this interface so the provider can be swapped later.
 */
public interface NotificationSender {

    void send(String recipient, String subject, String body);
}
