package com.toan.university_management.repository.notification;

import com.toan.university_management.entity.notification.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    Page<UserNotification> findAllByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    List<UserNotification> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
    Optional<UserNotification> findByNotificationIdAndUserId(Long notificationId, String userId);
    
    long countByUserIdAndReadFalse(String userId);

    @Modifying
    @Query("UPDATE UserNotification un SET un.read = true, un.readAt = :now WHERE un.userId = :userId AND un.read = false")
    void markAllAsReadByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserNotification un SET un.read = true, un.readAt = :now WHERE un.id = :id AND un.userId = :userId")
    void markAsReadByIdAndUserId(@Param("id") Long id, @Param("userId") String userId, @Param("now") LocalDateTime now);

    void deleteAllByNotificationId(Long notificationId);
}
