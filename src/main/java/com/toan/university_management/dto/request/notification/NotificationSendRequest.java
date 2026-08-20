package com.toan.university_management.dto.request.notification;

import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationSendRequest {

    @NotBlank(message = "Title is required")
    String title;

    @NotBlank(message = "Content is required")
    String content;

    @NotNull(message = "Type is required")
    NotificationType type;

    @Builder.Default
    NotificationPriority priority = NotificationPriority.NORMAL;

    @NotNull(message = "Target type is required")
    NotificationTargetType targetType;

    String targetValue;

    String actionUrl;
}
