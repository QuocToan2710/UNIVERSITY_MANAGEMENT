package com.toan.university_management.mapper.identity;

import com.toan.university_management.dto.request.identity.UserRequest;
import com.toan.university_management.dto.response.identity.UserResponse;
import com.toan.university_management.entity.identity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequest request);

    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest request);
}
