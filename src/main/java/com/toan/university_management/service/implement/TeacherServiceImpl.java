package com.toan.university_management.service.implement;


import com.toan.university_management.dto.request.TeacherRequest;
import com.toan.university_management.dto.response.TeacherResponse;
import com.toan.university_management.entity.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.TeacherMapper;
import com.toan.university_management.repository.TeacherRepository;
import com.toan.university_management.service.TeacherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TeacherServiceImpl implements TeacherService {
    TeacherRepository teacherRepository;
    TeacherMapper teacherMapper;

    @Override
    public TeacherResponse createTeacher(TeacherRequest request) {
        if (teacherRepository.existsByTeacherCode(request.getTeacherCode())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Teacher teacher = teacherMapper.toTeacher(request);
        return teacherMapper.toTeacherResponse(teacherRepository.save(teacher));
    }

    @Override
    public TeacherResponse getTeacherById(String id) {
        return teacherMapper.toTeacherResponse(teacherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @Override
    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(teacherMapper::toTeacherResponse)
                .toList();
    }

    @Override
    public TeacherResponse updateTeacher(String id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        teacherMapper.updateTeacher(teacher, request);

        return teacherMapper.toTeacherResponse(teacherRepository.save(teacher));
    }

    @Override
    public void deleteTeacher(String id) {
        if (!teacherRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        teacherRepository.deleteById(id);
    }

    @Override
    public List<TeacherResponse> getTeachersBySpecialization(String specialization) {
        List<Teacher> teachers = teacherRepository.findBySpecializationContainingIgnoreCase(specialization);
        return teacherMapper.toTeacherResponseList(teachers);
    }
}
