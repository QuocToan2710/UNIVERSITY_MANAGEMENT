package com.toan.university_management.repository.notification;

import com.toan.university_management.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndDeletedFalse(Long id);
    Page<Notification> findAllByDeletedFalse(Pageable pageable);
    List<Notification> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    boolean existsByNotificationCodeAndDeletedFalse(String notificationCode);
}
