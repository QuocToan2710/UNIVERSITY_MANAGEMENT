package com.toan.university_management.service;

import com.toan.university_management.dto.request.UserRequest;
import com.toan.university_management.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(String id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UserRequest request);
    UserResponse getMyInfo();

    void deleteUser(String  id);
}
