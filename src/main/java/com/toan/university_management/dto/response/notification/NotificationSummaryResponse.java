package com.toan.university_management.dto.response.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationSummaryResponse {
    long unreadCount;
    List<NotificationResponse> recentNotifications;
}
