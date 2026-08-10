package com.toan.university_management.service.masterdata.classgroup;

import com.toan.university_management.dto.request.masterdata.ClassGroupRequest;
import com.toan.university_management.dto.response.masterdata.ClassGroupResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ClassGroupMapper;
import com.toan.university_management.repository.masterdata.ClassGroupRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.service.masterdata.classgroup.ClassGroupService;
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
public class ClassGroupServiceImpl implements ClassGroupService {

    ClassGroupRepository classGroupRepository;
    TeacherRepository teacherRepository;
    ClassGroupMapper classGroupMapper;

    @Override
    public ClassGroupResponse createClassGroup(ClassGroupRequest request) {
        if (classGroupRepository.existsByClassCode(request.getClassCode()))
            throw new AppException(ErrorCode.CLASS_GROUP_EXISTED);

        ClassGroup classGroup = classGroupMapper.toClassGroup(request);

        if (request.getHomeroomTeacherId() != null && !request.getHomeroomTeacherId().isBlank()) {
            Teacher teacher = teacherRepository.findById(request.getHomeroomTeacherId())
                    .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
            classGroup.setHomeroomTeacher(teacher);
        }

        return classGroupMapper.toClassGroupResponse(classGroupRepository.save(classGroup));
    }

    @Override
    public ClassGroupResponse updateClassGroup(String id, ClassGroupRequest request) {
        ClassGroup classGroup = classGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));

        classGroupMapper.updateClassGroup(classGroup, request);

        if (request.getHomeroomTeacherId() != null && !request.getHomeroomTeacherId().isBlank()) {
            Teacher teacher = teacherRepository.findById(request.getHomeroomTeacherId())
                    .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
            classGroup.setHomeroomTeacher(teacher);
        } else {
            classGroup.setHomeroomTeacher(null);
        }

        return classGroupMapper.toClassGroupResponse(classGroupRepository.save(classGroup));
    }

    @Override
    public void deleteClassGroup(String id) {
        if (!classGroupRepository.existsById(id))
            throw new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND);
        classGroupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassGroupResponse getClassGroupById(String id) {
        ClassGroup cg = classGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));
        return classGroupMapper.toClassGroupResponse(cg);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassGroupResponse> getAllClassGroups(Pageable pageable) {
        return classGroupRepository.findAll(pageable)
                .map(classGroupMapper::toClassGroupResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassGroupResponse> getAllClassGroups() {
        return classGroupMapper.toClassGroupResponseList(
                classGroupRepository.findAllWithTeacher());
    }
}


