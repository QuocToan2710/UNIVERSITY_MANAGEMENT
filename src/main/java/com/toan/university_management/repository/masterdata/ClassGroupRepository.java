package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.ClassGroup;

public interface ClassGroupRepository extends BaseRepository<ClassGroup, Long> {
    boolean existsByClassCodeAndDeletedFalse(String classCode);
}
