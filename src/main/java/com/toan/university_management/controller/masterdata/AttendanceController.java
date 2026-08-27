package com.toan.university_management.controller.masterdata;

import com.toan.university_management.annotation.PermissionMeta;
import com.toan.university_management.dto.request.masterdata.AttendanceSessionRequest;
import com.toan.university_management.dto.request.masterdata.AutoGenerateSessionsRequest;
import com.toan.university_management.dto.request.masterdata.SubmitAttendanceRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.AttendanceRecordResponse;
import com.toan.university_management.dto.response.masterdata.AttendanceSessionResponse;
import com.toan.university_management.dto.response.masterdata.BannedStudentResponse;
import com.toan.university_management.dto.response.masterdata.StudentAttendanceSummaryResponse;
import com.toan.university_management.service.masterdata.attendance.AttendanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {

    AttendanceService attendanceService;

    @PostMapping("/sessions/auto-generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Tự động sinh các buổi học theo thời khóa biểu")
    public ApiResponse<List<AttendanceSessionResponse>> autoGenerateSessions(
            @Valid @RequestBody AutoGenerateSessionsRequest request) {
        return ApiResponse.<List<AttendanceSessionResponse>>builder()
                .message("Tự động sinh danh sách buổi học thành công")
                .result(attendanceService.autoGenerateSessions(request))
                .build();
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Tạo một buổi học hoặc buổi học bù mới")
    public ApiResponse<AttendanceSessionResponse> createSession(
            @Valid @RequestBody AttendanceSessionRequest request) {
        return ApiResponse.<AttendanceSessionResponse>builder()
                .message("Tạo buổi học thành công")
                .result(attendanceService.createSession(request))
                .build();
    }

    @PutMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Cập nhật thông tin buổi học")
    public ApiResponse<AttendanceSessionResponse> updateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody AttendanceSessionRequest request) {
        return ApiResponse.<AttendanceSessionResponse>builder()
                .message("Cập nhật buổi học thành công")
                .result(attendanceService.updateSession(sessionId, request))
                .build();
    }

    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Xóa buổi học")
    public ApiResponse<Void> deleteSession(@PathVariable Long sessionId) {
        attendanceService.deleteSession(sessionId);
        return ApiResponse.<Void>builder()
                .message("Xóa buổi học thành công")
                .build();
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Lấy danh sách các buổi học của lớp học phần")
    public ApiResponse<List<AttendanceSessionResponse>> getSessionsBySubjectClass(
            @RequestParam Long subjectClassId) {
        return ApiResponse.<List<AttendanceSessionResponse>>builder()
                .result(attendanceService.getSessionsBySubjectClass(subjectClassId))
                .build();
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Xem chi tiết thông tin buổi học")
    public ApiResponse<AttendanceSessionResponse> getSessionDetails(@PathVariable Long sessionId) {
        return ApiResponse.<AttendanceSessionResponse>builder()
                .result(attendanceService.getSessionDetails(sessionId))
                .build();
    }

    @GetMapping("/sessions/{sessionId}/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Lấy danh sách bảng điểm danh của buổi học")
    public ApiResponse<List<AttendanceRecordResponse>> getSessionRecords(@PathVariable Long sessionId) {
        return ApiResponse.<List<AttendanceRecordResponse>>builder()
                .result(attendanceService.getSessionRecords(sessionId))
                .build();
    }

    @PostMapping("/sessions/{sessionId}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Lưu và chốt điểm danh buổi học")
    public ApiResponse<AttendanceSessionResponse> submitAttendance(
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitAttendanceRequest request) {
        return ApiResponse.<AttendanceSessionResponse>builder()
                .message("Chốt điểm danh buổi học thành công")
                .result(attendanceService.submitAttendance(sessionId, request))
                .build();
    }

    @GetMapping("/my-summary")
    @PreAuthorize("isAuthenticated()")
    @PermissionMeta(module = "ATTENDANCE", description = "Sinh viên tự tra cứu tổng quan chuyên cần cá nhân")
    public ApiResponse<List<StudentAttendanceSummaryResponse>> getMyAttendanceSummary(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        return ApiResponse.<List<StudentAttendanceSummaryResponse>>builder()
                .result(attendanceService.getMyAttendanceSummary(semester, academicYear))
                .build();
    }

    @GetMapping("/my-details")
    @PreAuthorize("isAuthenticated()")
    @PermissionMeta(module = "ATTENDANCE", description = "Sinh viên xem chi tiết nhật ký điểm danh môn học")
    public ApiResponse<StudentAttendanceSummaryResponse> getMyAttendanceDetails(
            @RequestParam Long subjectClassId) {
        return ApiResponse.<StudentAttendanceSummaryResponse>builder()
                .result(attendanceService.getMyAttendanceDetails(subjectClassId))
                .build();
    }

    @GetMapping("/banned-students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "ATTENDANCE", description = "Lấy danh sách sinh viên bị cấm thi")
    public ApiResponse<List<BannedStudentResponse>> getBannedStudents(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Long subjectClassId) {
        return ApiResponse.<List<BannedStudentResponse>>builder()
                .result(attendanceService.getBannedStudents(semester, academicYear, subjectClassId))
                .build();
    }
}
