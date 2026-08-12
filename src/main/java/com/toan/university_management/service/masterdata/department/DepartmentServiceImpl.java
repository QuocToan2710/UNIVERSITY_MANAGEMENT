package com.toan.university_management.service.masterdata.department;

import com.toan.university_management.dto.request.masterdata.DepartmentRequest;
import com.toan.university_management.dto.response.masterdata.DepartmentResponse;
import com.toan.university_management.entity.masterdata.Department;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.DepartmentMapper;
import com.toan.university_management.repository.masterdata.DepartmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    DepartmentRepository departmentRepository;
    DepartmentMapper departmentMapper;

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByDepartmentCodeAndDeletedFalse(request.getDepartmentCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Department department = departmentMapper.toDepartment(request);
        department = departmentRepository.save(department);
        return departmentMapper.toDepartmentResponse(department);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return departmentMapper.toDepartmentResponse(department);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAllByDeletedFalse().stream()
                .map(departmentMapper::toDepartmentResponse)
                .toList();
    }

    @Override
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllByDeletedFalse(pageable)
                .map(departmentMapper::toDepartmentResponse);
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        departmentMapper.updateDepartment(department, request);
        department = departmentRepository.save(department);
        return departmentMapper.toDepartmentResponse(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        departmentRepository.deleteById(id);
    }
}
