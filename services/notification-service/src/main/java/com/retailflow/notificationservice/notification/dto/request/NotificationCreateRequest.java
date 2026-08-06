package com.retailflow.notificationservice.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NotificationCreateRequest {

    @NotBlank(message = "Recipient is required")
    @Size(max = 150, message = "Recipient cannot exceed 150 characters")
    private String recipient;

    @NotNull(message = "Channel is required")
    private String channel;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @Size(max = 2000, message = "Body cannot exceed 2000 characters")
    private String body;
}
