package com.retailflow.notificationservice.notification.service.imp;

import com.retailflow.notificationservice.common.exception.InvalidRequestException;
import com.retailflow.notificationservice.notification.dto.request.NotificationCreateRequest;
import com.retailflow.notificationservice.notification.entity.Notification;
import com.retailflow.notificationservice.notification.entity.NotificationChannel;
import com.retailflow.notificationservice.notification.entity.NotificationStatus;
import com.retailflow.notificationservice.notification.mapper.NotificationMapper;
import com.retailflow.notificationservice.notification.repository.NotificationRepository;
import com.retailflow.notificationservice.notification.sender.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationSender notificationSender;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                notificationRepository, notificationMapper, notificationSender);
    }

    private NotificationCreateRequest request() {
        NotificationCreateRequest request = new NotificationCreateRequest();
        request.setRecipient("store@retailflow.com");
        request.setChannel("EMAIL");
        request.setSubject("Low stock alert");
        request.setBody("Gold Bangle is below reorder level");
        return request;
    }

    @Test
    void createAndSend_shouldMarkSent() {
        Notification notification = new Notification();
        notification.setRecipient("store@retailflow.com");
        notification.setSubject("Low stock alert");
        notification.setBody("Gold Bangle is below reorder level");

        when(notificationMapper.toEntity(any(NotificationCreateRequest.class))).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        notificationService.createAndSend(request());

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        verify(notificationSender).send("store@retailflow.com", "Low stock alert", "Gold Bangle is below reorder level");
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(captor.getValue().getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(captor.getValue().getSentAt()).isNotNull();
    }

    @Test
    void createAndSend_shouldMarkFailed_whenSenderThrows() {
        Notification notification = new Notification();
        when(notificationMapper.toEntity(any(NotificationCreateRequest.class))).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });
        doThrow(new RuntimeException("SMTP down"))
                .when(notificationSender).send(any(), any(), any());

        notificationService.createAndSend(request());

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
    }

    @Test
    void createAndSend_shouldRejectInvalidChannel() {
        NotificationCreateRequest request = request();
        request.setChannel("PIGEON");

        assertThatThrownBy(() -> notificationService.createAndSend(request))
                .isInstanceOf(InvalidRequestException.class);
    }
}
