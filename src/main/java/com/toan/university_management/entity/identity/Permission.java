package com.toan.university_management.entity.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "permission", uniqueConstraints = {
    @UniqueConstraint(name = "uk_permission_code", columnNames = {"permission_code"})
})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

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
