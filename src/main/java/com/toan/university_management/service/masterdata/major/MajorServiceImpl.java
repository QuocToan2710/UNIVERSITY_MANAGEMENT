package com.toan.university_management.service.masterdata.major;

import com.toan.university_management.dto.request.masterdata.MajorRequest;
import com.toan.university_management.dto.response.masterdata.MajorResponse;
import com.toan.university_management.entity.masterdata.Department;
import com.toan.university_management.entity.masterdata.Major;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.MajorMapper;
import com.toan.university_management.repository.masterdata.DepartmentRepository;
import com.toan.university_management.repository.masterdata.MajorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class MajorServiceImpl implements MajorService {
    MajorRepository majorRepository;
    DepartmentRepository departmentRepository;
    MajorMapper majorMapper;

    @Override
    public MajorResponse createMajor(MajorRequest request) {
        if (majorRepository.existsByMajorCodeAndDeletedFalse(request.getMajorCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        Major major = majorMapper.toMajor(request);
        major = majorRepository.save(major);
        return enrichResponse(major);
    }

    @Override
    public MajorResponse getMajorById(Long id) {
        Major major = majorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
        return enrichResponse(major);
    }

    @Override
    public List<MajorResponse> getAllMajors() {
        return enrichResponses(majorRepository.findAllByDeletedFalse());
    }

    @Override
    public Page<MajorResponse> getAllMajors(Pageable pageable) {
        Page<Major> page = majorRepository.findAllByDeletedFalse(pageable);
        List<MajorResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        majorMapper.updateMajor(major, request);
        major = majorRepository.save(major);
        return enrichResponse(major);
    }

    @Override
    public void deleteMajor(Long id) {
        if (!majorRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.MAJOR_NOT_FOUND);
        }
        majorRepository.deleteById(id);
    }

    private MajorResponse enrichResponse(Major major) {
        MajorResponse res = majorMapper.toMajorResponse(major);
        if (major.getDepartmentId() != null) {
            departmentRepository.findByIdAndDeletedFalse(major.getDepartmentId()).ifPresent(d -> {
                res.setDepartmentName(d.getName());
            });
        }
        return res;
    }

    private List<MajorResponse> enrichResponses(List<Major> majors) {
        if (majors.isEmpty()) return Collections.emptyList();

        Set<Long> deptIds = majors.stream().map(Major::getDepartmentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Department> deptMap = departmentRepository.findAllById(deptIds)
                .stream().collect(Collectors.toMap(Department::getId, Function.identity()));

        return majors.stream().map(m -> {
            MajorResponse res = majorMapper.toMajorResponse(m);
            if (m.getDepartmentId() != null && deptMap.containsKey(m.getDepartmentId())) {
                res.setDepartmentName(deptMap.get(m.getDepartmentId()).getName());
            }
            return res;
        }).toList();
    }
}
