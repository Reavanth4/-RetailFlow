package com.retailflow.notificationservice.notification.service.imp;

import com.retailflow.notificationservice.common.exception.InvalidRequestException;
import com.retailflow.notificationservice.common.exception.ResourceNotFoundException;
import com.retailflow.notificationservice.notification.dto.request.NotificationCreateRequest;
import com.retailflow.notificationservice.notification.dto.response.NotificationResponse;
import com.retailflow.notificationservice.notification.entity.Notification;
import com.retailflow.notificationservice.notification.entity.NotificationChannel;
import com.retailflow.notificationservice.notification.entity.NotificationStatus;
import com.retailflow.notificationservice.notification.mapper.NotificationMapper;
import com.retailflow.notificationservice.notification.repository.NotificationRepository;
import com.retailflow.notificationservice.notification.sender.NotificationSender;
import com.retailflow.notificationservice.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSender notificationSender;

    @Override
    public NotificationResponse createAndSend(NotificationCreateRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setChannel(resolveChannel(request.getChannel()));
        notification.setStatus(NotificationStatus.PENDING);

        notification = notificationRepository.save(notification);

        try {
            notificationSender.send(
                    notification.getRecipient(),
                    notification.getSubject(),
                    notification.getBody());
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send notification [{}]: {}", notification.getId(), e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
        }

        notification = notificationRepository.save(notification);
        log.info("Notification [{}] created with status {}", notification.getId(), notification.getStatus());

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        return notificationMapper.toResponse(notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationMapper.toResponseList(notificationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByRecipient(String recipient) {
        return notificationMapper.toResponseList(
                notificationRepository.findByRecipient(recipient));
    }

    private NotificationChannel resolveChannel(String channel) {
        try {
            return NotificationChannel.valueOf(channel);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(
                    "Invalid channel: " + channel + ". Allowed: EMAIL, SMS, IN_APP");
        }
    }
}
