package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByIdAndDeletedFalse(Long id);
    Optional<Department> findByDepartmentCodeAndDeletedFalse(String departmentCode);
    Page<Department> findAllByDeletedFalse(Pageable pageable);
    List<Department> findAllByDeletedFalse();
    boolean existsByDepartmentCodeAndDeletedFalse(String departmentCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
