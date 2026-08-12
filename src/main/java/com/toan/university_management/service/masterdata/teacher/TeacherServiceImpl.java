package com.toan.university_management.service.masterdata.teacher;

import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.entity.masterdata.Department;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.TeacherMapper;
import com.toan.university_management.repository.masterdata.DepartmentRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
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
public class TeacherServiceImpl implements TeacherService {
    TeacherRepository teacherRepository;
    DepartmentRepository departmentRepository;
    TeacherMapper teacherMapper;

    @Override
    public TeacherResponse createTeacher(TeacherRequest request) {
        if (teacherRepository.existsByTeacherCodeAndDeletedFalse(request.getTeacherCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        Teacher teacher = teacherMapper.toTeacher(request);
        teacher = teacherRepository.save(teacher);
        return enrichResponse(teacher);
    }

    @Override
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        return enrichResponse(teacher);
    }

    @Override
    public List<TeacherResponse> getAllTeachers() {
        return enrichResponses(teacherRepository.findAllByDeletedFalse());
    }

    @Override
    public Page<TeacherResponse> getAllTeachers(Pageable pageable) {
        Page<Teacher> page = teacherRepository.findAllByDeletedFalse(pageable);
        List<TeacherResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        teacherMapper.updateTeacher(teacher, request);
        teacher = teacherRepository.save(teacher);
        return enrichResponse(teacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        teacherRepository.deleteById(id);
    }

    private TeacherResponse enrichResponse(Teacher teacher) {
        TeacherResponse res = teacherMapper.toTeacherResponse(teacher);
        if (teacher.getDepartmentId() != null) {
            departmentRepository.findByIdAndDeletedFalse(teacher.getDepartmentId()).ifPresent(d -> {
                res.setDepartmentName(d.getName());
            });
        }
        return res;
    }

    private List<TeacherResponse> enrichResponses(List<Teacher> teachers) {
        if (teachers.isEmpty()) return Collections.emptyList();

        Set<Long> deptIds = teachers.stream().map(Teacher::getDepartmentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Department> deptMap = departmentRepository.findAllById(deptIds)
                .stream().collect(Collectors.toMap(Department::getId, Function.identity()));

        return teachers.stream().map(t -> {
            TeacherResponse res = teacherMapper.toTeacherResponse(t);
            if (t.getDepartmentId() != null && deptMap.containsKey(t.getDepartmentId())) {
                res.setDepartmentName(deptMap.get(t.getDepartmentId()).getName());
            }
            return res;
        }).toList();
    }
}
