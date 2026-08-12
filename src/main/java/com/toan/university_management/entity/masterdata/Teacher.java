package com.toan.university_management.entity.masterdata;

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
@Table(name = "teacher",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_teacher_code_deleted", columnNames = {"teacher_code", "deleted"})
    },
    indexes = {
        @Index(name = "idx_teacher_department", columnList = "department_id")
    }
)
@SQLDelete(sql = "UPDATE teacher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "teacher_code", nullable = false)
    String teacherCode;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "degree")
    String degree;

    @Column(name = "department_id")
    Long departmentId;

    @Column(name = "user_id")
    String userId;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;
}
