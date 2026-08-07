package com.toan.university_management.mapper;


import com.toan.university_management.dto.request.UserRequest;
import com.toan.university_management.dto.response.UserResponse;
import com.toan.university_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    User toUser(UserRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest request);
}
