package com.toan.university_management.service.masterdata.classgroup;

import com.toan.university_management.dto.request.masterdata.ClassGroupRequest;
import com.toan.university_management.dto.response.masterdata.ClassGroupResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import com.toan.university_management.entity.masterdata.Major;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ClassGroupMapper;
import com.toan.university_management.repository.masterdata.ClassGroupRepository;
import com.toan.university_management.repository.masterdata.MajorRepository;
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
public class ClassGroupServiceImpl implements ClassGroupService {
    ClassGroupRepository classGroupRepository;
    TeacherRepository teacherRepository;
    MajorRepository majorRepository;
    com.toan.university_management.repository.masterdata.StudentRepository studentRepository;
    ClassGroupMapper classGroupMapper;

    @Override
    public ClassGroupResponse createClassGroup(ClassGroupRequest request) {
        if (classGroupRepository.existsByClassCodeAndDeletedFalse(request.getClassCode()))
            throw new AppException(ErrorCode.CLASS_GROUP_EXISTED);

        if (request.getMajorId() != null && !majorRepository.existsByIdAndDeletedFalse(request.getMajorId())) {
            throw new AppException(ErrorCode.MAJOR_NOT_FOUND);
        }
        if (request.getHomeroomTeacherId() != null && !teacherRepository.existsByIdAndDeletedFalse(request.getHomeroomTeacherId())) {
            throw new AppException(ErrorCode.TEACHER_NOT_FOUND);
        }

        ClassGroup classGroup = classGroupMapper.toClassGroup(request);
        if (classGroup.getMaxStudents() == null || classGroup.getMaxStudents() <= 0) {
            classGroup.setMaxStudents(50);
        }
        classGroup = classGroupRepository.save(classGroup);
        return enrichClassGroupResponse(classGroup);
    }

    @Override
    public ClassGroupResponse updateClassGroup(Long id, ClassGroupRequest request) {
        ClassGroup classGroup = classGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));

        if (request.getMajorId() != null && !majorRepository.existsByIdAndDeletedFalse(request.getMajorId())) {
            throw new AppException(ErrorCode.MAJOR_NOT_FOUND);
        }
        if (request.getHomeroomTeacherId() != null && !teacherRepository.existsByIdAndDeletedFalse(request.getHomeroomTeacherId())) {
            throw new AppException(ErrorCode.TEACHER_NOT_FOUND);
        }

        classGroupMapper.updateClassGroup(classGroup, request);
        if (classGroup.getMaxStudents() == null || classGroup.getMaxStudents() <= 0) {
            classGroup.setMaxStudents(50);
        }
        classGroup = classGroupRepository.save(classGroup);
        return enrichClassGroupResponse(classGroup);
    }

    @Override
    public void deleteClassGroup(Long id) {
        if (!classGroupRepository.existsByIdAndDeletedFalse(id))
            throw new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND);
        classGroupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassGroupResponse getClassGroupById(Long id) {
        ClassGroup cg = classGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));
        return enrichClassGroupResponse(cg);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassGroupResponse> getAllClassGroups(Pageable pageable) {
        Page<ClassGroup> classGroupPage = classGroupRepository.findAllByDeletedFalse(pageable);
        List<ClassGroupResponse> content = enrichClassGroupResponses(classGroupPage.getContent());
        return new PageImpl<>(content, pageable, classGroupPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassGroupResponse> getAllClassGroups() {
        List<ClassGroup> list = classGroupRepository.findAllByDeletedFalse();
        return enrichClassGroupResponses(list);
    }

    private ClassGroupResponse enrichClassGroupResponse(ClassGroup cg) {
        ClassGroupResponse res = classGroupMapper.toClassGroupResponse(cg);
        if (cg.getHomeroomTeacherId() != null) {
            teacherRepository.findByIdAndDeletedFalse(cg.getHomeroomTeacherId()).ifPresent(t -> res.setHomeroomTeacherName(t.getFullName()));
        }
        if (cg.getMajorId() != null) {
            majorRepository.findByIdAndDeletedFalse(cg.getMajorId()).ifPresent(m -> res.setMajorName(m.getName()));
        }
        long count = studentRepository.countByClassGroupIdAndDeletedFalse(cg.getId());
        res.setCurrentStudents((int) count);
        res.setMaxStudents(cg.getMaxStudents() != null && cg.getMaxStudents() > 0 ? cg.getMaxStudents() : 50);
        return res;
    }

    private List<ClassGroupResponse> enrichClassGroupResponses(List<ClassGroup> list) {
        if (list.isEmpty()) return Collections.emptyList();

        Set<Long> teacherIds = list.stream().map(ClassGroup::getHomeroomTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> majorIds = list.stream().map(ClassGroup::getMajorId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, Teacher> teacherMap = teacherRepository.findAllByIdInAndDeletedFalse(teacherIds).stream().collect(Collectors.toMap(Teacher::getId, Function.identity()));
        Map<Long, Major> majorMap = majorRepository.findAllByIdInAndDeletedFalse(majorIds).stream().collect(Collectors.toMap(Major::getId, Function.identity()));

        Map<Long, Integer> studentCountMap = new HashMap<>();
        try {
            List<Object[]> countRows = studentRepository.countStudentsGroupedByClassGroup();
            for (Object[] row : countRows) {
                if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                    Long cgId = ((Number) row[0]).longValue();
                    int cnt = ((Number) row[1]).intValue();
                    studentCountMap.put(cgId, cnt);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to batch count students: {}", e.getMessage());
        }

        return list.stream().map(cg -> {
            ClassGroupResponse res = classGroupMapper.toClassGroupResponse(cg);
            if (cg.getHomeroomTeacherId() != null && teacherMap.containsKey(cg.getHomeroomTeacherId())) {
                res.setHomeroomTeacherName(teacherMap.get(cg.getHomeroomTeacherId()).getFullName());
            }
            if (cg.getMajorId() != null && majorMap.containsKey(cg.getMajorId())) {
                res.setMajorName(majorMap.get(cg.getMajorId()).getName());
            }
            int cur = studentCountMap.getOrDefault(cg.getId(), 0);
            res.setCurrentStudents(cur);
            res.setMaxStudents(cg.getMaxStudents() != null && cg.getMaxStudents() > 0 ? cg.getMaxStudents() : 50);
            return res;
        }).toList();
    }
}
