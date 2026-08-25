package com.toan.university_management.entity.identity;

import com.toan.university_management.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "permission", uniqueConstraints = {
    @UniqueConstraint(name = "uk_permission_code", columnNames = {"permission_code"})
})
public class Permission extends BaseEntity {

    @Column(name = "permission_code", nullable = false)
    String permissionCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "method")
    String method;

    @Column(name = "endpoint")
    String endpoint;

    @Column(name = "module")
    String module;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    boolean isPublic = false;
}
