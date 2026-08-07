package com.toan.university_management.service.implement;

import com.toan.university_management.dto.request.StudentRequest;
import com.toan.university_management.dto.response.StudentResponse;
import com.toan.university_management.entity.Student;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.StudentMapper;
import com.toan.university_management.repository.StudentRepository;
import com.toan.university_management.service.StudentService;
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

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByStudentCode(request.getStudentCode())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Student student = studentMapper.toStudent(request);

        return studentMapper.toStudentResponse(studentRepository.save(student));
    }

    @Override
    public StudentResponse getStudentById(String id) {
        Student student = studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return studentMapper.toStudentResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAllWithCourses();
        return studentMapper.toStudentResponseList(students);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(String id, StudentRequest request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        studentMapper.updateStudent(student, request);

        return studentMapper.toStudentResponse(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        if (!studentRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        studentRepository.deleteById(id);
    }


}
