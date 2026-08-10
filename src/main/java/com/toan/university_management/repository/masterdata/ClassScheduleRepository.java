package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.ClassSchedule;
import com.toan.university_management.enums.WeekDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, String> {

    Page<ClassSchedule> findAll(Pageable pageable);

    List<ClassSchedule> findByTeacherIdAndSemesterAndAcademicYear(
            String teacherId, String semester, String academicYear);

    List<ClassSchedule> findByClassGroupIdAndSemesterAndAcademicYear(
            String classGroupId, String semester, String academicYear);

    List<ClassSchedule> findByCourseIdAndSemesterAndAcademicYear(
            String courseId, String semester, String academicYear);

    /** Kiểm tra trùng lịch giảng viên (time overlap), loại trừ id hiện tại khi update */
    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s " +
           "WHERE s.teacher.id = :teacherId " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND s.semester = :semester " +
           "AND s.academicYear = :academicYear " +
           "AND s.id <> :excludeId " +
           "AND (s.startTime < :endTime AND s.endTime > :startTime)")
    boolean existsTeacherConflict(@Param("teacherId") String teacherId,
                                   @Param("dayOfWeek") WeekDay dayOfWeek,
                                   @Param("startTime") LocalTime startTime,
                                   @Param("endTime") LocalTime endTime,
                                   @Param("semester") String semester,
                                   @Param("academicYear") String academicYear,
                                   @Param("excludeId") String excludeId);

    /** Kiểm tra trùng phòng học (time overlap), loại trừ id hiện tại khi update */
    @Query("SELECT COUNT(s) > 0 FROM ClassSchedule s " +
           "WHERE s.room = :room " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND s.semester = :semester " +
           "AND s.academicYear = :academicYear " +
           "AND s.id <> :excludeId " +
           "AND (s.startTime < :endTime AND s.endTime > :startTime)")
    boolean existsRoomConflict(@Param("room") String room,
                                @Param("dayOfWeek") WeekDay dayOfWeek,
                                @Param("startTime") LocalTime startTime,
                                @Param("endTime") LocalTime endTime,
                                @Param("semester") String semester,
                                @Param("academicYear") String academicYear,
                                @Param("excludeId") String excludeId);

    /** Lịch của sinh viên thông qua lớp học của sinh viên đó */
    @Query("SELECT s FROM ClassSchedule s " +
           "WHERE s.classGroup.id = " +
           "  (SELECT st.classGroup.id FROM Student st WHERE st.id = :studentId) " +
           "AND s.semester = :semester " +
           "AND s.academicYear = :academicYear")
    List<ClassSchedule> findByStudentId(@Param("studentId") String studentId,
                                         @Param("semester") String semester,
                                         @Param("academicYear") String academicYear);
}


