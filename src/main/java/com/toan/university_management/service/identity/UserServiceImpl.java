package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.UserRequest;
import com.toan.university_management.dto.response.identity.RoleResponse;
import com.toan.university_management.dto.response.identity.UserResponse;
import com.toan.university_management.entity.identity.Role;
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

        // Map roles safely
        Set<String> roleCodes = request.getRoles();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equalsIgnoreCase(a.getAuthority()) || "ADMIN".equalsIgnoreCase(a.getAuthority()));

        if (roleCodes == null || roleCodes.isEmpty()) {
            roleCodes = Set.of("ROLE_USER");
        } else if (!isAdmin) {
            boolean hasPrivilegedRole = roleCodes.stream()
                    .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r) || "ADMIN".equalsIgnoreCase(r)
                                   || "ROLE_TEACHER".equalsIgnoreCase(r) || "TEACHER".equalsIgnoreCase(r));
            if (hasPrivilegedRole) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        final Long createdUserId = user.getId();
        for (String rKey : roleCodes) {
            resolveRole(rKey).ifPresent(role -> {
                userRoleRepository.save(UserRole.builder()
                        .userId(createdUserId)
                        .roleId(role.getId())
                        .build());
            });
        }

        return enrichUserResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return enrichUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllByDeletedFalse();
        return enrichUserResponses(users);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAllByDeletedFalse(pageable);
        List<UserResponse> content = enrichUserResponses(userPage.getContent());
        return new org.springframework.data.domain.PageImpl<>(content, pageable, userPage.getTotalElements());
    }

    @Override
    public UserResponse updateUser(UserRequest request) {
        if (request.getId() == null) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        User user = userRepository.findByIdAndDeletedFalse(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user = userRepository.save(user);

        if (request.getRoles() != null) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null && authentication.isAuthenticated() &&
                    authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equalsIgnoreCase(a.getAuthority()) || "ADMIN".equalsIgnoreCase(a.getAuthority()));
            if (!isAdmin) {
                boolean hasPrivilegedRole = request.getRoles().stream()
                        .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r) || "ADMIN".equalsIgnoreCase(r)
                                       || "ROLE_TEACHER".equalsIgnoreCase(r) || "TEACHER".equalsIgnoreCase(r));
                if (hasPrivilegedRole) {
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            }
            userRoleRepository.deleteByUserId(user.getId());
            for (String roleKey : request.getRoles()) {
                Optional<Role> roleOpt = resolveRole(roleKey);
                if (roleOpt.isPresent()) {
                    UserRole userRole = UserRole.builder()
                            .userId(user.getId())
                            .roleId(roleOpt.get().getId())
                            .build();
                    userRoleRepository.save(userRole);
                }
            }
        }

        return enrichUserResponse(user);
    }

    @Override
    public UserResponse updateUserRoles(Long id, List<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        final Long targetUserId = user.getId();
        userRoleRepository.deleteByUserId(targetUserId);
        if (roleNames != null) {
            for (String rKey : roleNames) {
                resolveRole(rKey).ifPresent(role -> {
                    userRoleRepository.save(UserRole.builder()
                            .userId(targetUserId)
                            .roleId(role.getId())
                            .build());
                });
            }
        }

        return enrichUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String name = authentication.getName();

        User user = userRepository.findByUsername(name)
                .or(() -> userRepository.findByUsernameIgnoreCase(name))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return enrichUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setDeleted(true);
        userRepository.save(user);
    }

    private Optional<Role> resolveRole(String roleKey) {
        try {
            Long rId = Long.parseLong(roleKey);
            Optional<Role> roleOpt = roleRepository.findById(rId);
            if (roleOpt.isPresent()) return roleOpt;
        } catch (NumberFormatException ignored) {}

        return roleRepository.findByRoleCode(roleKey)
                .or(() -> roleRepository.findByName(roleKey));
    }

    private UserResponse enrichUserResponse(User user) {
        UserResponse response = userMapper.toUserResponse(user);
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        Set<RoleResponse> roles = new HashSet<>();
        if (!roleIds.isEmpty()) {
            List<Role> roleEntities = roleRepository.findAllByIdIn(roleIds);
            for (Role r : roleEntities) {
                roles.add(RoleResponse.builder()
                        .id(r.getId())
                        .roleCode(r.getRoleCode())
                        .name(r.getName())
                        .description(r.getDescription())
                        .build());
            }
        }

        response.setRoles(roles);
        return response;
    }

    private List<UserResponse> enrichUserResponses(List<User> users) {
        if (users.isEmpty()) return Collections.emptyList();
        Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
        List<UserRole> allUserRoles = userRoleRepository.findByUserIdIn(userIds);

        Set<Long> allRoleIds = allUserRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        Map<Long, Role> roleMap = roleRepository.findAllByIdIn(allRoleIds).stream()
                .collect(Collectors.toMap(Role::getId, r -> r));

        Map<Long, Set<RoleResponse>> userRoleMap = new HashMap<>();
        for (UserRole ur : allUserRoles) {
            Role r = roleMap.get(ur.getRoleId());
            if (r != null) {
                userRoleMap.computeIfAbsent(ur.getUserId(), k -> new HashSet<>())
                        .add(RoleResponse.builder()
                                .id(r.getId())
                                .roleCode(r.getRoleCode())
                                .name(r.getName())
                                .description(r.getDescription())
                                .build());
            }
        }

        return users.stream().map(u -> {
            UserResponse res = userMapper.toUserResponse(u);
            res.setRoles(userRoleMap.getOrDefault(u.getId(), Collections.emptySet()));
            return res;
        }).toList();
    }
}
