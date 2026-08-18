package com.toan.university_management.service.masterdata.student;

import com.toan.university_management.dto.request.masterdata.StudentRequest;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import com.toan.university_management.entity.masterdata.Major;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.enums.StudentStatus;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.StudentMapper;
import com.toan.university_management.repository.masterdata.ClassGroupRepository;
import com.toan.university_management.repository.masterdata.MajorRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
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
public class StudentServiceImpl implements StudentService {
    StudentRepository studentRepository;
    ClassGroupRepository classGroupRepository;
    MajorRepository majorRepository;
    StudentMapper studentMapper;

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByStudentCodeAndDeletedFalse(request.getStudentCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Student student = studentMapper.toStudent(request);

        if (request.getClassGroupId() != null && !classGroupRepository.existsByIdAndDeletedFalse(request.getClassGroupId())) {
            throw new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND);
        }

        if (request.getMajorId() != null && !majorRepository.existsByIdAndDeletedFalse(request.getMajorId())) {
            throw new AppException(ErrorCode.MAJOR_NOT_FOUND);
        }

        if (request.getStatus() != null) {
            student.setStatus(request.getStatus());
        } else {
            student.setStatus(StudentStatus.ACTIVE);
        }

        student = studentRepository.save(student);
        return enrichStudentResponse(student);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return enrichStudentResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return enrichStudentResponses(students);
    }

    @Override
    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        Page<Student> studentPage = studentRepository.findAllByDeletedFalse(pageable);
        List<StudentResponse> content = enrichStudentResponses(studentPage.getContent());
        return new PageImpl<>(content, pageable, studentPage.getTotalElements());
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        studentMapper.updateStudent(student, request);

        if (request.getClassGroupId() != null) {
            if (!classGroupRepository.existsByIdAndDeletedFalse(request.getClassGroupId())) {
                throw new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND);
            }
        }

        if (request.getStatus() != null) {
            student.setStatus(request.getStatus());
        }

        student = studentRepository.save(student);
        return enrichStudentResponse(student);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public com.toan.university_management.dto.response.BasePaginationRS<StudentResponse> search(com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ search) {
        if (search == null) search = new com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ();
        int page = Math.max(0, search.getPageNumber());
        int size = search.getPageSize() > 0 ? search.getPageSize() : 10;

        String kw = search.getKeyword() != null ? search.getKeyword().trim().toLowerCase() : "";
        String codeFilter = search.getStudentCode() != null ? search.getStudentCode().trim().toLowerCase() : "";
        String nameFilter = search.getFullName() != null ? search.getFullName().trim().toLowerCase() : "";
        String emailFilter = search.getEmail() != null ? search.getEmail().trim().toLowerCase() : "";
        Long majorFilter = search.getMajorId();
        Long classGroupFilter = search.getClassGroupId();

        List<StudentResponse> all = enrichStudentResponses(studentRepository.findAllByDeletedFalse()).stream()
                .filter(s -> {
                    if (!kw.isEmpty()) {
                        String full = ((s.getStudentCode() != null ? s.getStudentCode() : "") + " "
                                + (s.getFullName() != null ? s.getFullName() : "") + " "
                                + (s.getEmail() != null ? s.getEmail() : "") + " "
                                + (s.getClassGroupName() != null ? s.getClassGroupName() : "") + " "
                                + (s.getMajorName() != null ? s.getMajorName() : "")).toLowerCase();
                        if (!full.contains(kw)) return false;
                    }
                    if (!codeFilter.isEmpty()) {
                        if (s.getStudentCode() == null || !s.getStudentCode().toLowerCase().contains(codeFilter)) return false;
                    }
                    if (!nameFilter.isEmpty()) {
                        if (s.getFullName() == null || !s.getFullName().toLowerCase().contains(nameFilter)) return false;
                    }
                    if (!emailFilter.isEmpty()) {
                        if (s.getEmail() == null || !s.getEmail().toLowerCase().contains(emailFilter)) return false;
                    }
                    if (majorFilter != null && majorFilter > 0) {
                        if (s.getMajorId() == null || !s.getMajorId().equals(majorFilter)) return false;
                    }
                    if (classGroupFilter != null && classGroupFilter > 0) {
                        if (s.getClassGroupId() == null || !s.getClassGroupId().equals(classGroupFilter)) return false;
                    }
                    return true;
                })
                .toList();

        long count = all.size();
        int start = page * size;
        List<StudentResponse> pageList = start < count ? all.subList(start, Math.min(start + size, (int) count)) : List.of();

        int totalPage = (int) (count / size);
        if (count % size != 0) totalPage++;

        com.toan.university_management.dto.response.BasePaginationRS<StudentResponse> outputs = new com.toan.university_management.dto.response.BasePaginationRS<>();
        outputs.setItems(pageList);
        outputs.setTotalCount(count);
        outputs.setTotalPage(totalPage);
        return outputs;
    }

    @Override
    public List<StudentResponse> export(com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ search) {
        com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ copy = search != null ? search : new com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ();
        copy.setPageNumber(0);
        copy.setPageSize(Integer.MAX_VALUE);
        return search(copy).getItems();
    }

    private StudentResponse enrichStudentResponse(Student student) {
        StudentResponse response = studentMapper.toStudentResponse(student);
        if (student.getClassGroupId() != null) {
            classGroupRepository.findByIdAndDeletedFalse(student.getClassGroupId()).ifPresent(cg -> {
                response.setClassCode(cg.getClassCode());
                response.setClassGroupName(cg.getClassName());
            });
        }
        if (student.getMajorId() != null) {
            majorRepository.findByIdAndDeletedFalse(student.getMajorId()).ifPresent(m -> {
                response.setMajorName(m.getName());
            });
        }
        return response;
    }

    private List<StudentResponse> enrichStudentResponses(List<Student> students) {
        if (students.isEmpty()) return Collections.emptyList();

        Set<Long> classGroupIds = students.stream()
                .map(Student::getClassGroupId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> majorIds = students.stream()
                .map(Student::getMajorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ClassGroup> classGroupMap = classGroupRepository.findAllByIdInAndDeletedFalse(classGroupIds)
                .stream().collect(Collectors.toMap(ClassGroup::getId, Function.identity()));

        Map<Long, Major> majorMap = majorRepository.findAllByIdInAndDeletedFalse(majorIds)
                .stream().collect(Collectors.toMap(Major::getId, Function.identity()));

        return students.stream().map(s -> {
            StudentResponse res = studentMapper.toStudentResponse(s);
            if (s.getClassGroupId() != null && classGroupMap.containsKey(s.getClassGroupId())) {
                ClassGroup cg = classGroupMap.get(s.getClassGroupId());
                res.setClassCode(cg.getClassCode());
                res.setClassGroupName(cg.getClassName());
            }
            if (s.getMajorId() != null && majorMap.containsKey(s.getMajorId())) {
                res.setMajorName(majorMap.get(s.getMajorId()).getName());
            }
            return res;
        }).toList();
    }
}
