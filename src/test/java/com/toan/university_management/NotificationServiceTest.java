package com.toan.university_management;

import com.toan.university_management.dto.request.notification.NotificationSendRequest;
import com.toan.university_management.dto.response.notification.NotificationResponse;
import com.toan.university_management.dto.response.notification.NotificationSummaryResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.notification.NotificationRepository;
import com.toan.university_management.repository.notification.UserNotificationRepository;
import com.toan.university_management.service.notification.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        String suffix = String.valueOf(System.nanoTime());
        testUser = userRepository.save(User.builder()
                .username("test_notify_" + suffix)
                .password("password123")
                .email("test_notify_" + suffix + "@university.edu.vn")
                .fullName("Test Notification User " + suffix)
                .userCode("USR_NOTIF_" + suffix)
                .build());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testUser.getUsername(),
                        "password123",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (testUser != null && testUser.getId() != null) {
            userNotificationRepository.deleteAll();
            notificationRepository.deleteAll();
            userRepository.deleteById(testUser.getId());
        }
    }

    @Test
    void testSendAndReceiveNotification() {
        // 1. Send system notification to testUser
        notificationService.sendSystemNotification(
                "Lịch thi mới môn Java",
                "Ca thi bắt đầu lúc 8:00 sáng ngày 20/08",
                NotificationType.EXAM,
                NotificationPriority.HIGH,
                NotificationTargetType.USER,
                String.valueOf(testUser.getId()),
                "/schedule/exam"
        );

        // 2. Check unread count
        long unread = notificationService.getMyUnreadCount();
        assertTrue(unread >= 1, "Unread count should be at least 1");

        // 3. Check summary
        NotificationSummaryResponse summary = notificationService.getMyNotificationSummary();
        assertNotNull(summary);
        assertTrue(summary.getUnreadCount() >= 1);
        assertFalse(summary.getRecentNotifications().isEmpty());

        NotificationResponse firstNotif = summary.getRecentNotifications().get(0);
        assertEquals("Lịch thi mới môn Java", firstNotif.getTitle());
        assertEquals(NotificationType.EXAM, firstNotif.getType());
        assertFalse(firstNotif.isRead());

        // 4. Mark as read
        notificationService.markAsRead(firstNotif.getId());

        // 5. Verify unread count decreases
        long newUnread = notificationService.getMyUnreadCount();
        assertEquals(unread - 1, newUnread);
    }

    @Test
    void testMarkAllAsRead() {
        notificationService.sendSystemNotification(
                "Thông báo 1", "Nội dung 1", NotificationType.GENERAL, NotificationPriority.NORMAL,
                NotificationTargetType.USER, String.valueOf(testUser.getId()), null
        );
        notificationService.sendSystemNotification(
                "Thông báo 2", "Nội dung 2", NotificationType.SCHEDULE, NotificationPriority.HIGH,
                NotificationTargetType.USER, String.valueOf(testUser.getId()), null
        );

        long unreadBefore = notificationService.getMyUnreadCount();
        assertTrue(unreadBefore >= 2);

        notificationService.markAllAsRead();
        long unreadAfter = notificationService.getMyUnreadCount();
        assertEquals(0, unreadAfter);
    }
}
