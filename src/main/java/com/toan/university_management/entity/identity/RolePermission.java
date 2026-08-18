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
    @Index(name = "idx_role_perm_role_id", columnList = "role_id"),
    @Index(name = "idx_role_perm_perm_id", columnList = "permission_id")
})
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "role_id", nullable = false)
    Long roleId;

    @Column(name = "permission_id", nullable = false)
    Long permissionId;
}
