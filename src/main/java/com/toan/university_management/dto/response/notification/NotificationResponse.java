package com.toan.university_management.dto.response.notification;

import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;                    // UserNotification ID (or Notification ID)
    Long notificationId;
    String notificationCode;
    String title;
    String content;
    NotificationType type;
    NotificationPriority priority;
    NotificationTargetType targetType;
    String targetValue;
    Long senderId;
    String senderName;
    String actionUrl;
    boolean read;
    LocalDateTime readAt;
    LocalDateTime createdAt;
}
