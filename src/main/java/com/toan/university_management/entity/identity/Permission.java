package com.toan.university_management.entity.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Permission {
    @Id
    @Column(name = "name")
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

