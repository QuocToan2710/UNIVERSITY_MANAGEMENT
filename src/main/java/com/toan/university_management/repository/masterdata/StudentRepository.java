package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.dto.reports.StudentReportDTO;
import com.toan.university_management.entity.masterdata.Student;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends BaseRepository<Student, Long> {
    Optional<Student> findByUserIdAndDeletedFalse(Long userId);
    Optional<Student> findByStudentCodeAndDeletedFalse(String studentCode);
    Optional<Student> findByEmailAndDeletedFalse(String email);
    boolean existsByStudentCodeAndDeletedFalse(String studentCode);
    long countByClassGroupIdAndDeletedFalse(Long classGroupId);
    List<Student> findAllByClassGroupIdAndDeletedFalse(Long classGroupId);
    List<Student> findAllByClassGroupIdInAndDeletedFalse(Collection<Long> classGroupIds);

    @Query("SELECT s.classGroupId, COUNT(s) FROM Student s WHERE s.deleted = false AND s.classGroupId IS NOT NULL GROUP BY s.classGroupId")
    List<Object[]> countStudentsGroupedByClassGroup();

    @Query("SELECT new com.toan.university_management.dto.reports.StudentReportDTO(CAST(s.id AS string), s.studentCode, s.fullName, s.dob, s.gender, s.phoneNumber, s.email, s.address) FROM Student s WHERE s.deleted = false")
    List<StudentReportDTO> getAllStudentForReport();
}
