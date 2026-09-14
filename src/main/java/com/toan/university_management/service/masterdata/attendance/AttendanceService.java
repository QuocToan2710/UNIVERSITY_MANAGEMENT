package com.toan.university_management.service.masterdata.attendance;

import com.toan.university_management.model.masterdata.AttendanceSessionRequest;
import com.toan.university_management.model.masterdata.AutoGenerateSessionsRequest;
import com.toan.university_management.model.masterdata.SubmitAttendanceRequest;
import com.toan.university_management.model.masterdata.AttendanceRecordResponse;
import com.toan.university_management.model.masterdata.AttendanceSessionResponse;
import com.toan.university_management.model.masterdata.BannedStudentResponse;
import com.toan.university_management.model.masterdata.StudentAttendanceSummaryResponse;

import java.util.List;

public interface AttendanceService {

    List<AttendanceSessionResponse> autoGenerateSessions(AutoGenerateSessionsRequest request);

    AttendanceSessionResponse createSession(AttendanceSessionRequest request);

    AttendanceSessionResponse updateSession(Long sessionId, AttendanceSessionRequest request);

    void deleteSession(Long sessionId);

    List<AttendanceSessionResponse> getSessionsBySubjectClass(Long subjectClassId);

    AttendanceSessionResponse getSessionDetails(Long sessionId);

    List<AttendanceRecordResponse> getSessionRecords(Long sessionId);

    AttendanceSessionResponse submitAttendance(Long sessionId, SubmitAttendanceRequest request);

    List<StudentAttendanceSummaryResponse> getMyAttendanceSummary(String semester, String academicYear);

    StudentAttendanceSummaryResponse getMyAttendanceDetails(Long subjectClassId);

    List<BannedStudentResponse> getBannedStudents(String semester, String academicYear, Long subjectClassId);
}
