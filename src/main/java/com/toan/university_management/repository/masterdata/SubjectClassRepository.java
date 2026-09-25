package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.SubjectClass;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubjectClassRepository extends JpaRepository<SubjectClass, Long> {
    Optional<SubjectClass> findByIdAndDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sc FROM SubjectClass sc WHERE sc.id = :id AND sc.deleted = false")
    Optional<SubjectClass> findByIdWithLock(@Param("id") Long id);

    Page<SubjectClass> findAllByDeletedFalse(Pageable pageable);
    List<SubjectClass> findAllByDeletedFalse();
    List<SubjectClass> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    List<SubjectClass> findAllBySemesterAndAcademicYearAndDeletedFalse(String semester, String academicYear);
    List<SubjectClass> findAllBySemesterAndDeletedFalse(String semester);
    List<SubjectClass> findAllByAcademicYearAndDeletedFalse(String academicYear);
    boolean existsBySubjectClassCodeAndDeletedFalse(String subjectClassCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
