package com.toan.university_management.dto.response.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    Long id;
    String roleCode;
    String name;
    String description;
    Set<PermissionResponse> permissions;
}
