package com.toan.university_management.service.notification;

import com.toan.university_management.dto.request.notification.NotificationSendRequest;
import com.toan.university_management.dto.response.notification.NotificationResponse;
import com.toan.university_management.dto.response.notification.NotificationSummaryResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.identity.UserRole;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.entity.notification.Notification;
import com.toan.university_management.entity.notification.UserNotification;
import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.identity.UserRoleRepository;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.repository.notification.NotificationRepository;
import com.toan.university_management.repository.notification.UserNotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;
    UserNotificationRepository userNotificationRepository;
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    RoleRepository roleRepository;
    StudentRepository studentRepository;
    TeacherRepository teacherRepository;
    EnrollmentRepository enrollmentRepository;
    SubjectClassRepository subjectClassRepository;

    @Override
    public NotificationResponse sendNotification(NotificationSendRequest request) {
        String currentUserId = null;
        String currentSenderName = "Hệ thống";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            var userOpt = userRepository.findByUsername(auth.getName());
            if (userOpt.isPresent()) {
                currentUserId = userOpt.get().getId();
                currentSenderName = userOpt.get().getFullName() != null ? userOpt.get().getFullName() : userOpt.get().getUsername();
            }
        }

        Notification notification = Notification.builder()
                .notificationCode("NOTIF_" + System.currentTimeMillis())
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .priority(request.getPriority() != null ? request.getPriority() : NotificationPriority.NORMAL)
                .targetType(request.getTargetType())
                .targetValue(request.getTargetValue())
                .senderId(currentUserId)
                .senderName(currentSenderName)
                .actionUrl(request.getActionUrl())
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);

        // Phân giải danh sách người nhận và tạo UserNotification
        Set<String> recipientUserIds = resolveRecipientUserIds(notification.getTargetType(), notification.getTargetValue());
        if (!recipientUserIds.isEmpty()) {
            final Long notifId = notification.getId();
            final LocalDateTime now = LocalDateTime.now();
            List<UserNotification> userNotifs = recipientUserIds.stream()
                    .map(uid -> UserNotification.builder()
                            .notificationId(notifId)
                            .userId(uid)
                            .read(false)
                            .createdAt(now)
                            .build())
                    .collect(Collectors.toList());
            userNotificationRepository.saveAll(userNotifs);
        }

        return mapToResponse(notification, null);
    }

    @Override
    public void sendSystemNotification(
            String title,
            String content,
            NotificationType type,
            NotificationPriority priority,
            NotificationTargetType targetType,
            String targetValue,
            String actionUrl
    ) {
        Notification notification = Notification.builder()
                .notificationCode("NOTIF_" + System.currentTimeMillis())
                .title(title)
                .content(content)
                .type(type)
                .priority(priority != null ? priority : NotificationPriority.NORMAL)
                .targetType(targetType)
                .targetValue(targetValue)
                .senderId("SYSTEM")
                .senderName("Hệ thống Đào tạo")
                .actionUrl(actionUrl)
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);

        Set<String> recipientUserIds = resolveRecipientUserIds(targetType, targetValue);
        if (!recipientUserIds.isEmpty()) {
            final Long notifId = notification.getId();
            final LocalDateTime now = LocalDateTime.now();
            List<UserNotification> userNotifs = recipientUserIds.stream()
                    .map(uid -> UserNotification.builder()
                            .notificationId(notifId)
                            .userId(uid)
                            .read(false)
                            .createdAt(now)
                            .build())
                    .collect(Collectors.toList());
            userNotificationRepository.saveAll(userNotifs);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        String currentUserId = getCurrentUserId();
        Page<UserNotification> unPage = userNotificationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId, pageable);
        if (unPage.isEmpty()) {
            return Page.empty(pageable);
        }

        Set<Long> notifIds = unPage.getContent().stream()
                .map(UserNotification::getNotificationId)
                .collect(Collectors.toSet());

        Map<Long, Notification> notifMap = notificationRepository.findAllByIdInAndDeletedFalse(notifIds).stream()
                .collect(Collectors.toMap(Notification::getId, Function.identity()));

        List<NotificationResponse> responses = unPage.getContent().stream()
                .filter(un -> notifMap.containsKey(un.getNotificationId()))
                .map(un -> mapToResponse(notifMap.get(un.getNotificationId()), un))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, unPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationSummaryResponse getMyNotificationSummary() {
        String currentUserId = getCurrentUserId();
        long unreadCount = userNotificationRepository.countByUserIdAndReadFalse(currentUserId);
        List<UserNotification> recentUserNotifs = userNotificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(currentUserId);

        List<NotificationResponse> recentResponses = Collections.emptyList();
        if (!recentUserNotifs.isEmpty()) {
            Set<Long> notifIds = recentUserNotifs.stream()
                    .map(UserNotification::getNotificationId)
                    .collect(Collectors.toSet());

            Map<Long, Notification> notifMap = notificationRepository.findAllByIdInAndDeletedFalse(notifIds).stream()
                    .collect(Collectors.toMap(Notification::getId, Function.identity()));

            recentResponses = recentUserNotifs.stream()
                    .filter(un -> notifMap.containsKey(un.getNotificationId()))
                    .map(un -> mapToResponse(notifMap.get(un.getNotificationId()), un))
                    .collect(Collectors.toList());
        }

        return NotificationSummaryResponse.builder()
                .unreadCount(unreadCount)
                .recentNotifications(recentResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long getMyUnreadCount() {
        String currentUserId = getCurrentUserId();
        return userNotificationRepository.countByUserIdAndReadFalse(currentUserId);
    }

    @Override
    public void markAsRead(Long userNotificationId) {
        String currentUserId = getCurrentUserId();
        userNotificationRepository.markAsReadByIdAndUserId(userNotificationId, currentUserId, LocalDateTime.now());
    }

    @Override
    public void markAllAsRead() {
        String currentUserId = getCurrentUserId();
        userNotificationRepository.markAllAsReadByUserId(currentUserId, LocalDateTime.now());
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findByIdAndDeletedFalse(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        notificationRepository.deleteById(notification.getId());
        userNotificationRepository.deleteAllByNotificationId(notification.getId());
    }

    // --- HELPER METHODS ---

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepository.findByUsername(auth.getName())
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private Set<String> resolveRecipientUserIds(NotificationTargetType targetType, String targetValue) {
        Set<String> userIds = new HashSet<>();
        if (targetType == null) return userIds;

        switch (targetType) {
            case ALL -> {
                userIds.addAll(userRepository.findAllByDeletedFalse().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet()));
            }
            case ROLE -> {
                if (targetValue != null && !targetValue.isBlank()) {
                    String val = targetValue.trim().toUpperCase();
                    String roleCode = val.startsWith("ROLE_") ? val : "ROLE_" + val;
                    String simpleName = val.replace("ROLE_", "");

                    roleRepository.findByRoleCode(roleCode)
                            .or(() -> roleRepository.findByRoleCode(val))
                            .or(() -> roleRepository.findByName(simpleName))
                            .or(() -> roleRepository.findByName(val))
                            .ifPresent(role -> {
                                List<UserRole> urs = userRoleRepository.findByRoleId(role.getId());
                                userIds.addAll(urs.stream().map(UserRole::getUserId).collect(Collectors.toSet()));
                            });
                }
            }
            case USER -> {
                if (targetValue != null && !targetValue.isBlank()) {
                    String val = targetValue.trim();
                    userRepository.findById(val)
                            .or(() -> userRepository.findByUsername(val))
                            .or(() -> userRepository.findByUsernameIgnoreCase(val))
                            .map(User::getId)
                            .ifPresent(userIds::add);

                    if (userIds.isEmpty()) {
                        // Check studentCode / teacherCode
                        studentRepository.findAllByDeletedFalse().stream()
                                .filter(s -> val.equalsIgnoreCase(s.getStudentCode()) || val.equalsIgnoreCase(s.getEmail()))
                                .map(Student::getUserId)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .ifPresent(userIds::add);
                    }

                    if (userIds.isEmpty()) {
                        teacherRepository.findAllByDeletedFalse().stream()
                                .filter(t -> val.equalsIgnoreCase(t.getTeacherCode()) || val.equalsIgnoreCase(t.getEmail()))
                                .map(Teacher::getUserId)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .ifPresent(userIds::add);
                    }
                }
            }
            case SUBJECT_CLASS -> {
                if (targetValue != null && !targetValue.isBlank()) {
                    try {
                        Long scId = Long.parseLong(targetValue.trim());
                        // 1. Sinh viên trong lớp học phần
                        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdInAndDeletedFalse(List.of(scId));
                        Set<Long> studentIds = enrollments.stream()
                                .map(Enrollment::getStudentId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                        if (!studentIds.isEmpty()) {
                            studentRepository.findAllByIdInAndDeletedFalse(studentIds).stream()
                                    .map(Student::getUserId)
                                    .filter(Objects::nonNull)
                                    .forEach(userIds::add);
                        }

                        // 2. Giảng viên dạy lớp học phần
                        subjectClassRepository.findByIdAndDeletedFalse(scId).ifPresent(sc -> {
                            if (sc.getTeacherId() != null) {
                                teacherRepository.findByIdAndDeletedFalse(sc.getTeacherId()).ifPresent(t -> {
                                    if (t.getUserId() != null) userIds.add(t.getUserId());
                                });
                            }
                        });
                    } catch (NumberFormatException ignored) {}
                }
            }
            case CLASS_GROUP -> {
                if (targetValue != null && !targetValue.isBlank()) {
                    try {
                        Long cgId = Long.parseLong(targetValue.trim());
                        studentRepository.findAllByDeletedFalse().stream()
                                .filter(s -> cgId.equals(s.getClassGroupId()))
                                .map(Student::getUserId)
                                .filter(Objects::nonNull)
                                .forEach(userIds::add);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return userIds;
    }

    private NotificationResponse mapToResponse(Notification n, UserNotification un) {
        return NotificationResponse.builder()
                .id(un != null ? un.getId() : n.getId())
                .notificationId(n.getId())
                .notificationCode(n.getNotificationCode())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .priority(n.getPriority())
                .targetType(n.getTargetType())
                .targetValue(n.getTargetValue())
                .senderId(n.getSenderId())
                .senderName(n.getSenderName())
                .actionUrl(n.getActionUrl())
                .read(un != null && un.isRead())
                .readAt(un != null ? un.getReadAt() : null)
                .createdAt(un != null && un.getCreatedAt() != null ? un.getCreatedAt() : n.getCreatedAt())
                .build();
    }
}
