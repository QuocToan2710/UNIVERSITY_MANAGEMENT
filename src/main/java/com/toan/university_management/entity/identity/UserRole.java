package com.toan.university_management.entity.identity;

import com.toan.university_management.common.entity.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "user_role", indexes = {
    @Index(name = "idx_user_role_user", columnList = "user_id"),
    @Index(name = "idx_user_role_role_id", columnList = "role_id")
})
public class UserRole extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "role_id", nullable = false)
    Long roleId;
}
