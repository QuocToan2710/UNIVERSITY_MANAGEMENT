package com.toan.university_management.service.notification;

import com.toan.university_management.dto.request.notification.NotificationSendRequest;
import com.toan.university_management.dto.response.notification.NotificationResponse;
import com.toan.university_management.dto.response.notification.NotificationSummaryResponse;
import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationSendRequest request);

    void sendSystemNotification(
            String title,
            String content,
            NotificationType type,
            NotificationPriority priority,
            NotificationTargetType targetType,
            String targetValue,
            String actionUrl
    );

    Page<NotificationResponse> getMyNotifications(Pageable pageable);

    NotificationSummaryResponse getMyNotificationSummary();

    long getMyUnreadCount();

    void markAsRead(Long userNotificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);
}
