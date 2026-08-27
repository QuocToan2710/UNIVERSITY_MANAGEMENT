package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.ClassSchedule;
import com.toan.university_management.enums.WeekDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    Optional<ClassSchedule> findByIdAndDeletedFalse(Long id);
    Page<ClassSchedule> findAllByDeletedFalse(Pageable pageable);
    List<ClassSchedule> findAllByDeletedFalse();
    boolean existsByScheduleCodeAndDeletedFalse(String scheduleCode);
    boolean existsByIdAndDeletedFalse(Long id);

    /** Lấy lịch theo teacherId + semester + academicYear */
    List<ClassSchedule> findAllByTeacherIdAndSemesterAndAcademicYearAndDeletedFalse(
            Long teacherId, String semester, String academicYear);

    /** Lấy lịch theo subjectClassId */
    List<ClassSchedule> findAllBySubjectClassIdAndDeletedFalse(Long subjectClassId);

    /** Lấy lịch theo danh sách subjectClassId (dùng cho student lookup qua enrollment) */
    List<ClassSchedule> findAllBySubjectClassIdInAndDeletedFalse(Collection<Long> subjectClassIds);

    /** Lấy lịch theo danh sách subjectClassId + semester + academicYear */
    @Query("SELECT s FROM ClassSchedule s WHERE s.deleted = false " +
           "AND s.subjectClassId IN :subjectClassIds " +
           "AND (:semester IS NULL OR s.semester = :semester) " +
           "AND (:academicYear IS NULL OR s.academicYear = :academicYear)")
    List<ClassSchedule> findBySubjectClassIdsAndFilters(
            @Param("subjectClassIds") Collection<Long> subjectClassIds,
            @Param("semester") String semester,
            @Param("academicYear") String academicYear);

    /** Kiểm tra conflict giáo viên: trùng dayOfWeek và khoảng thời gian, loại trừ scheduleId hiện tại khi update */
    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s WHERE s.deleted = false " +
           "AND s.teacherId = :teacherId " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND s.startTime < :endTime AND s.endTime > :startTime " +
           "AND (:excludeId IS NULL OR s.id <> :excludeId)")
    boolean existsTeacherConflict(
            @Param("teacherId") Long teacherId,
            @Param("dayOfWeek") WeekDay dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    /** Kiểm tra conflict phòng học: trùng room + dayOfWeek + khoảng thời gian */
    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s WHERE s.deleted = false " +
           "AND s.room = :room " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND s.startTime < :endTime AND s.endTime > :startTime " +
           "AND (:excludeId IS NULL OR s.id <> :excludeId)")
    boolean existsRoomConflict(
            @Param("room") String room,
            @Param("dayOfWeek") WeekDay dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);
}
