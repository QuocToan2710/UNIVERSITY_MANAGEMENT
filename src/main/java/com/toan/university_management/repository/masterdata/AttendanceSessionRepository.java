package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.AttendanceSession;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends BaseRepository<AttendanceSession, Long> {
    List<AttendanceSession> findAllBySubjectClassIdAndDeletedFalseOrderBySessionNumberAsc(Long subjectClassId);
    List<AttendanceSession> findAllBySubjectClassIdInAndDeletedFalse(Collection<Long> subjectClassIds);
    List<AttendanceSession> findAllByTeacherIdAndDeletedFalse(Long teacherId);
    Optional<AttendanceSession> findBySessionCodeAndDeletedFalse(String sessionCode);
    Optional<AttendanceSession> findBySubjectClassIdAndSessionNumberAndDeletedFalse(Long subjectClassId, Integer sessionNumber);
    long countBySubjectClassIdAndDeletedFalse(Long subjectClassId);
    boolean existsBySubjectClassIdAndSessionDateAndDeletedFalse(Long subjectClassId, LocalDate sessionDate);
}
