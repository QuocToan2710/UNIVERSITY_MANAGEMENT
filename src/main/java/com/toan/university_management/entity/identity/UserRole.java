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
@Table(name = "user_role", indexes = {
    @Index(name = "idx_user_role_user", columnList = "user_id"),
    @Index(name = "idx_user_role_role_id", columnList = "role_id")
})
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "role_id", nullable = false)
    Long roleId;
}
