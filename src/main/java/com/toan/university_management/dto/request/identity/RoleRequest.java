package com.toan.university_management.dto.request.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    Long id;
    String roleCode;
    String name;
    String description;
    Set<String> permissions;
}
