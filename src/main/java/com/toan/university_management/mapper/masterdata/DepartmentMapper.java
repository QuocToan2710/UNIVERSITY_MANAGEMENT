package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.model.masterdata.DepartmentRequest;
import com.toan.university_management.model.masterdata.DepartmentResponse;
import com.toan.university_management.entity.masterdata.Department;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    @Mapping(target = "deleted", ignore = true)
    Department toDepartment(DepartmentRequest request);

    DepartmentResponse toDepartmentResponse(Department department);

    List<DepartmentResponse> toDepartmentResponseList(List<Department> list);

    @Mapping(target = "deleted", ignore = true)
    void updateDepartment(@MappingTarget Department department, DepartmentRequest request);
}
