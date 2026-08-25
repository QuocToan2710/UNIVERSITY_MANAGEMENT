package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.UserRequest;
import com.toan.university_management.dto.response.identity.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(UserRequest request);
    UserResponse updateUserRoles(Long id, List<String> roleNames);
    UserResponse getMyInfo();

    void deleteUser(Long id);
}


