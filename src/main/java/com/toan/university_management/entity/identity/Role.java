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
@Table(name = "role", uniqueConstraints = {
    @UniqueConstraint(name = "uk_role_code", columnNames = {"role_code"})
})
public class Role extends BaseEntity {

    @Column(name = "role_code", nullable = false)
    String roleCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;
}
