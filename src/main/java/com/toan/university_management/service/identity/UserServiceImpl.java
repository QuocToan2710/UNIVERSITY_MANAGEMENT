package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.UserRequest;
import com.toan.university_management.dto.response.identity.RoleResponse;
import com.toan.university_management.dto.response.identity.UserResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.identity.UserRole;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.identity.UserMapper;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.identity.UserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        if (user.getUserCode() == null || user.getUserCode().isBlank()) {
            user.setUserCode("USR_" + System.currentTimeMillis());
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        // Map roles
        Set<String> roleCodes = request.getRoles();
        if (roleCodes == null || roleCodes.isEmpty()) {
            roleCodes = Set.of("ROLE_USER");
        }

        for (String roleCode : roleCodes) {
            userRoleRepository.save(UserRole.builder()
                    .userId(user.getId())
                    .roleCode(roleCode)
                    .build());
        }

        return enrichUserResponse(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return enrichUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return enrichUserResponses(users);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> content = enrichUserResponses(userPage.getContent());
        return new org.springframework.data.domain.PageImpl<>(content, pageable, userPage.getTotalElements());
    }

    @Override
    public UserResponse updateUser(UserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user = userRepository.save(user);

        if (request.getRoles() != null) {
            userRoleRepository.deleteByUserId(user.getId());
            for (String roleCode : request.getRoles()) {
                userRoleRepository.save(UserRole.builder()
                        .userId(user.getId())
                        .roleCode(roleCode)
                        .build());
            }
        }

        return enrichUserResponse(user);
    }

    @Override
    public UserResponse updateUserRoles(String id, List<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRoleRepository.deleteByUserId(user.getId());
        for (String roleCode : roleNames) {
            userRoleRepository.save(UserRole.builder()
                    .userId(user.getId())
                    .roleCode(roleCode)
                    .build());
        }

        return enrichUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .or(() -> userRepository.findByUsernameIgnoreCase(name))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return enrichUserResponse(user);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userRoleRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private UserResponse enrichUserResponse(User user) {
        UserResponse response = userMapper.toUserResponse(user);
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        Set<RoleResponse> roles = new HashSet<>();

        for (UserRole ur : userRoles) {
            String code = ur.getRoleCode();
            if (code != null && !code.isBlank()) {
                String name = code.startsWith("ROLE_") ? code.substring("ROLE_".length()) : code;
                roles.add(RoleResponse.builder().roleCode(code).name(name).build());
            }
        }

        if (roles.isEmpty()) {
            if ("admin".equalsIgnoreCase(user.getUsername())) {
                roles.add(RoleResponse.builder().roleCode("ROLE_ADMIN").name("ADMIN").build());
            } else if ("teacher".equalsIgnoreCase(user.getUsername())) {
                roles.add(RoleResponse.builder().roleCode("ROLE_TEACHER").name("TEACHER").build());
            } else if ("student".equalsIgnoreCase(user.getUsername())) {
                roles.add(RoleResponse.builder().roleCode("ROLE_STUDENT").name("STUDENT").build());
            } else {
                roles.add(RoleResponse.builder().roleCode("ROLE_USER").name("USER").build());
            }
        }

        response.setRoles(roles);
        return response;
    }

    private List<UserResponse> enrichUserResponses(List<User> users) {
        if (users.isEmpty()) return Collections.emptyList();
        Set<String> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
        List<UserRole> allUserRoles = userRoleRepository.findByUserIdIn(userIds);
        Map<String, Set<RoleResponse>> userRoleMap = new HashMap<>();

        for (UserRole ur : allUserRoles) {
            String code = ur.getRoleCode();
            if (code != null && !code.isBlank()) {
                String name = code.startsWith("ROLE_") ? code.substring("ROLE_".length()) : code;
                userRoleMap.computeIfAbsent(ur.getUserId(), k -> new HashSet<>())
                        .add(RoleResponse.builder().roleCode(code).name(name).build());
            }
        }

        return users.stream().map(u -> {
            UserResponse res = userMapper.toUserResponse(u);
            Set<RoleResponse> roles = userRoleMap.getOrDefault(u.getId(), Collections.emptySet());
            if (roles.isEmpty()) {
                roles = new HashSet<>();
                if ("admin".equalsIgnoreCase(u.getUsername())) {
                    roles.add(RoleResponse.builder().roleCode("ROLE_ADMIN").name("ADMIN").build());
                } else if ("teacher".equalsIgnoreCase(u.getUsername())) {
                    roles.add(RoleResponse.builder().roleCode("ROLE_TEACHER").name("TEACHER").build());
                } else if ("student".equalsIgnoreCase(u.getUsername())) {
                    roles.add(RoleResponse.builder().roleCode("ROLE_STUDENT").name("STUDENT").build());
                } else {
                    roles.add(RoleResponse.builder().roleCode("ROLE_USER").name("USER").build());
                }
            }
            res.setRoles(roles);
            return res;
        }).toList();
    }
}
