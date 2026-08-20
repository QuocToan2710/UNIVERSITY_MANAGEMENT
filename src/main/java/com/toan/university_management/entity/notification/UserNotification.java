package com.toan.university_management.entity.notification;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "user_notification",
    indexes = {
        @Index(name = "idx_user_notification_user", columnList = "user_id, is_read"),
        @Index(name = "idx_user_notification_created", columnList = "created_at")
    }
)
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "notification_id", nullable = false)
    Long notificationId;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    boolean read = false;

    @Column(name = "read_at")
    LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
}
