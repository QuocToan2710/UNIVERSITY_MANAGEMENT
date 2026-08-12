package com.toan.university_management.repository.masterdata;

import com.toan.university_management.dto.reports.StudentReportDTO;
import com.toan.university_management.entity.masterdata.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByIdAndDeletedFalse(Long id);
    Page<Student> findAllByDeletedFalse(Pageable pageable);
    List<Student> findAllByDeletedFalse();
    List<Student> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    Optional<Student> findByUserIdAndDeletedFalse(String userId);
    boolean existsByStudentCodeAndDeletedFalse(String studentCode);
    boolean existsByIdAndDeletedFalse(Long id);

    @Query("SELECT new com.toan.university_management.dto.reports.StudentReportDTO(CAST(s.id AS string), s.studentCode, s.fullName, s.dob, s.gender, s.phoneNumber, s.email, s.address) FROM Student s WHERE s.deleted = false")
    List<StudentReportDTO> getAllStudentForReport();
}
