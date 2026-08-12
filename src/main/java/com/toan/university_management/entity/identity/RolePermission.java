package com.toan.university_management.entity.identity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "role_permission", indexes = {
    @Index(name = "idx_role_perm_role_code", columnList = "role_code"),
    @Index(name = "idx_role_perm_perm_code", columnList = "permission_code")
})
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "role_code", nullable = false)
    String roleCode;

    @Column(name = "permission_code", nullable = false)
    String permissionCode;
}
