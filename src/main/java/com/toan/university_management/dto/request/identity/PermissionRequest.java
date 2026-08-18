package com.toan.university_management.dto.request.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {
    Long id;
    String permissionCode;
    String name;
    String description;
    String method;
    String endpoint;
    String module;
    boolean isPublic;
}
