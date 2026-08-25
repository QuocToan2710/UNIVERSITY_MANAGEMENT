package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Major;

public interface MajorRepository extends BaseRepository<Major, Long> {
    boolean existsByMajorCodeAndDeletedFalse(String majorCode);
}
