package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.ClassGroup;

import java.util.List;

public interface ClassGroupRepository extends BaseRepository<ClassGroup, Long> {
    boolean existsByClassCodeAndDeletedFalse(String classCode);
    List<ClassGroup> findAllByMajorIdAndDeletedFalse(Long majorId);
}

