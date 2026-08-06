package com.retailflow.notificationservice.notification.mapper;

import com.retailflow.notificationservice.notification.dto.request.NotificationCreateRequest;
import com.retailflow.notificationservice.notification.dto.response.NotificationResponse;
import com.retailflow.notificationservice.notification.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationCreateRequest request);

    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
