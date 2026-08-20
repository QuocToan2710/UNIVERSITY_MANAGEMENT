package com.toan.university_management.entity.notification;

import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "notification",
    indexes = {
        @Index(name = "idx_notification_target", columnList = "target_type, target_value"),
        @Index(name = "idx_notification_created", columnList = "created_at")
    }
)
@SQLDelete(sql = "UPDATE notification SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "notification_code", nullable = false)
    String notificationCode;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @Builder.Default
    NotificationPriority priority = NotificationPriority.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    NotificationTargetType targetType;

    @Column(name = "target_value")
    String targetValue;

    @Column(name = "sender_id")
    String senderId;

    @Column(name = "sender_name")
    String senderName;

    @Column(name = "action_url")
    String actionUrl;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
