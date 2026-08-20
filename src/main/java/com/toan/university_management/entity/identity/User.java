package com.toan.university_management.entity.identity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_username_deleted", columnNames = {"username", "deleted_key"})
})
@SQLDelete(sql = "UPDATE user SET deleted = true, deleted_key = id WHERE id = ?")
@SQLRestriction("deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "user_code")
    String userCode;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "email")
    String email;

    @Column(name = "full_name")
    String fullName;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
