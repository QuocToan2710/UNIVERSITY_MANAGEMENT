package com.toan.university_management.dto.response.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {
    Long id;
    String permissionCode;
    String name;
    String description;
    String method;
    String endpoint;
    String module;
    boolean isPublic;
}
