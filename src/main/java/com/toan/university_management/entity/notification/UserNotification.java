package com.toan.university_management.entity.notification;

import com.toan.university_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "user_notification",
    indexes = {
        @Index(name = "idx_user_notification_user", columnList = "user_id, is_read"),
        @Index(name = "idx_user_notification_created", columnList = "created_at")
    }
)
public class UserNotification extends BaseEntity {

    @Column(name = "notification_id", nullable = false)
    Long notificationId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    boolean read = false;

    @Column(name = "read_at")
    LocalDateTime readAt;
}
