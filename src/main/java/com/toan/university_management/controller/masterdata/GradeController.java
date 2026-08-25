package com.toan.university_management.controller.masterdata;

import com.toan.university_management.annotation.PermissionMeta;
import com.toan.university_management.dto.request.masterdata.GradeBatchUpdateRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.StudentTranscriptResponse;
import com.toan.university_management.dto.response.masterdata.SubjectClassGradeSummaryResponse;
import com.toan.university_management.service.masterdata.grade.GradeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeController {

    GradeService gradeService;

    @GetMapping("/subject-classes/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "GRADE", description = "Xem bảng điểm lớp học phần")
    public ApiResponse<SubjectClassGradeSummaryResponse> getSubjectClassGrades(@PathVariable Long classId) {
        return ApiResponse.<SubjectClassGradeSummaryResponse>builder()
                .result(gradeService.getSubjectClassGrades(classId))
                .build();
    }

    @PutMapping("/subject-classes/{classId}/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "GRADE", description = "Cập nhật điểm theo danh sách lớp học phần")
    public ApiResponse<SubjectClassGradeSummaryResponse> updateBatchGrades(
            @PathVariable Long classId,
            @Valid @RequestBody GradeBatchUpdateRequest request) {
        return ApiResponse.<SubjectClassGradeSummaryResponse>builder()
                .message("Cập nhật bảng điểm thành công")
                .result(gradeService.updateBatchGrades(classId, request))
                .build();
    }

    @PostMapping("/subject-classes/{classId}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "GRADE", description = "Chốt nộp bảng điểm lớp học phần")
    public ApiResponse<SubjectClassGradeSummaryResponse> submitGrades(@PathVariable Long classId) {
        return ApiResponse.<SubjectClassGradeSummaryResponse>builder()
                .message("Bảng điểm đã được gửi lên Phòng Đào tạo duyệt")
                .result(gradeService.submitGrades(classId))
                .build();
    }

    @PostMapping("/subject-classes/{classId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @PermissionMeta(module = "GRADE", description = "Phê duyệt và công bố điểm cho sinh viên")
    public ApiResponse<SubjectClassGradeSummaryResponse> publishGrades(@PathVariable Long classId) {
        return ApiResponse.<SubjectClassGradeSummaryResponse>builder()
                .message("Đã công bố điểm cho sinh viên")
                .result(gradeService.publishGrades(classId))
                .build();
    }

    @PostMapping("/subject-classes/{classId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    @PermissionMeta(module = "GRADE", description = "Khóa sổ điểm lớp học phần")
    public ApiResponse<SubjectClassGradeSummaryResponse> lockGrades(@PathVariable Long classId) {
        return ApiResponse.<SubjectClassGradeSummaryResponse>builder()
                .message("Đã khóa sổ bảng điểm")
                .result(gradeService.lockGrades(classId))
                .build();
    }

    @GetMapping("/student/{studentId}/transcript")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PermissionMeta(module = "GRADE", description = "Xem bảng điểm và GPA/CPA của sinh viên")
    public ApiResponse<StudentTranscriptResponse> getStudentTranscript(@PathVariable Long studentId) {
        return ApiResponse.<StudentTranscriptResponse>builder()
                .result(gradeService.getStudentTranscript(studentId))
                .build();
    }

    @GetMapping("/my-transcript")
    @PreAuthorize("isAuthenticated()")
    @PermissionMeta(module = "GRADE", description = "Sinh viên tự tra cứu bảng điểm cá nhân", isPublic = false)
    public ApiResponse<StudentTranscriptResponse> getMyTranscript() {
        return ApiResponse.<StudentTranscriptResponse>builder()
                .result(gradeService.getMyTranscript())
                .build();
    }
}
