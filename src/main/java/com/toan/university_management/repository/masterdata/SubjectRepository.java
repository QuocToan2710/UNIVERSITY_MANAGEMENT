package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Subject;

import java.util.Optional;

public interface SubjectRepository extends BaseRepository<Subject, Long> {
    Optional<Subject> findBySubjectCodeAndDeletedFalse(String subjectCode);
    boolean existsBySubjectCodeAndDeletedFalse(String subjectCode);
}
