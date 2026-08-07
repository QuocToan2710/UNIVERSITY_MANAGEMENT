package com.toan.university_management.entity;

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
    String name;

    String description;

    String method;

    String endpoint;

    String module;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    boolean isPublic = false;
}
