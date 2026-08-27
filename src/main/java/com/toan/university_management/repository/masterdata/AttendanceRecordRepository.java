package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.AttendanceRecord;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends BaseRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findAllBySessionIdAndDeletedFalse(Long sessionId);
    List<AttendanceRecord> findAllBySessionIdInAndDeletedFalse(Collection<Long> sessionIds);
    List<AttendanceRecord> findAllByEnrollmentIdAndDeletedFalse(Long enrollmentId);
    List<AttendanceRecord> findAllByStudentIdAndDeletedFalse(Long studentId);
    Optional<AttendanceRecord> findBySessionIdAndStudentIdAndDeletedFalse(Long sessionId, Long studentId);
    List<AttendanceRecord> findAllByStudentIdAndSessionIdInAndDeletedFalse(Long studentId, Collection<Long> sessionIds);
    void deleteAllBySessionId(Long sessionId);
}
