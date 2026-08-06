package com.retailflow.notificationservice.notification.controller;

import com.retailflow.notificationservice.common.response.ApiResponse;
import com.retailflow.notificationservice.notification.dto.request.NotificationCreateRequest;
import com.retailflow.notificationservice.notification.dto.response.NotificationResponse;
import com.retailflow.notificationservice.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<NotificationResponse> createAndSend(
            @Valid @RequestBody NotificationCreateRequest request) {

        return ApiResponse.success(
                "Notification sent successfully",
                notificationService.createAndSend(request)
        );
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getAllNotifications() {

        return ApiResponse.success(
                "Notifications fetched successfully",
                notificationService.getAllNotifications()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getNotificationById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Notification fetched successfully",
                notificationService.getNotificationById(id)
        );
    }

    @GetMapping("/recipient/{recipient}")
    public ApiResponse<List<NotificationResponse>> getNotificationsByRecipient(
            @PathVariable String recipient) {

        return ApiResponse.success(
                "Notifications fetched successfully",
                notificationService.getNotificationsByRecipient(recipient)
        );
    }
}
