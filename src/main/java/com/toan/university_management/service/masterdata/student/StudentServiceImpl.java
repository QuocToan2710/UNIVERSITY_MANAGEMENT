package com.toan.university_management.service.masterdata.student;

import com.toan.university_management.dto.request.masterdata.StudentRequest;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.enums.StudentStatus;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.StudentMapper;
import com.toan.university_management.repository.masterdata.ClassGroupRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.service.masterdata.student.StudentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentServiceImpl implements StudentService {

    StudentRepository studentRepository;
    StudentMapper studentMapper;
    ClassGroupRepository classGroupRepository;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Student student = studentMapper.toStudent(request);

        // Gắn lớp học nếu có
        if (request.getClassGroupId() != null && !request.getClassGroupId().isBlank()) {
            ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                    .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));
            student.setClassGroup(classGroup);
        }

        // Gắn trạng thái
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                student.setStatus(StudentStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                student.setStatus(StudentStatus.ACTIVE);
            }
        } else {
            student.setStatus(StudentStatus.ACTIVE);
        }

        return studentMapper.toStudentResponse(studentRepository.save(student));
    }

    @Override
    public StudentResponse getStudentById(String id) {
        Student student = studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return studentMapper.toStudentResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAllWithCourses();
        return studentMapper.toStudentResponseList(students);
    }

    @Override
    public org.springframework.data.domain.Page<StudentResponse> getAllStudents(org.springframework.data.domain.Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(studentMapper::toStudentResponse);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(String id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        studentMapper.updateStudent(student, request);

        // Cập nhật lớp học
        if (request.getClassGroupId() != null && !request.getClassGroupId().isBlank()) {
            ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                    .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));
            student.setClassGroup(classGroup);
        } else {
            student.setClassGroup(null);
        }

        // Cập nhật trạng thái
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                student.setStatus(StudentStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                // giữ nguyên status cũ
            }
        }

        return studentMapper.toStudentResponse(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        if (!studentRepository.existsById(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }
        studentRepository.deleteById(id);
    }
}



