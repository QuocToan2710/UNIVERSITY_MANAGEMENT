package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Department;

import java.util.Optional;

public interface DepartmentRepository extends BaseRepository<Department, Long> {
    Optional<Department> findByDepartmentCodeAndDeletedFalse(String departmentCode);
    boolean existsByDepartmentCodeAndDeletedFalse(String departmentCode);
}
