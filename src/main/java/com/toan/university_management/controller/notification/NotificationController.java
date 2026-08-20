package com.toan.university_management.controller.notification;

import com.toan.university_management.dto.request.notification.NotificationSendRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.notification.NotificationResponse;
import com.toan.university_management.dto.response.notification.NotificationSummaryResponse;
import com.toan.university_management.service.notification.NotificationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    NotificationService notificationService;

    @GetMapping("/my")
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ApiResponse.<Page<NotificationResponse>>builder()
                .result(notificationService.getMyNotifications(pageable))
                .build();
    }

    @GetMapping("/summary")
    public ApiResponse<NotificationSummaryResponse> getMyNotificationSummary() {
        return ApiResponse.<NotificationSummaryResponse>builder()
                .result(notificationService.getMyNotificationSummary())
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getMyUnreadCount() {
        return ApiResponse.<Long>builder()
                .result(notificationService.getMyUnreadCount())
                .build();
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.<Void>builder()
                .message("Notification marked as read")
                .build();
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<Void>builder()
                .message("All notifications marked as read")
                .build();
    }

    @PostMapping("/send")
    public ApiResponse<NotificationResponse> sendNotification(
            @RequestBody @Valid NotificationSendRequest request
    ) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.sendNotification(request))
                .message("Notification sent successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .build();
    }
}
