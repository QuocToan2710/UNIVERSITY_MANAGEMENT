package com.toan.university_management.entity.identity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_username_deleted", columnNames = {"username", "deleted"})
})
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "email")
    String email;

    @Column(name = "full_name")
    String fullName;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    Set<Role> roles;
}

