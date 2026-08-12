package com.toan.university_management.entity.masterdata;

import com.toan.university_management.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "student", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_code_deleted", columnNames = {"student_code", "deleted"})
    },
    indexes = {
        @Index(name = "idx_student_class_group", columnList = "class_group_id"),
        @Index(name = "idx_student_major", columnList = "major_id"),
        @Index(name = "idx_student_user", columnList = "user_id")
    }
)
@SQLDelete(sql = "UPDATE student SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "student_code", nullable = false)
    String studentCode;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "dob")
    Date dob;

    @Column(name = "gender")
    String gender;

    @Column(name = "address")
    String address;

    @Column(name = "enrollment_year")
    String enrollmentYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    StudentStatus status = StudentStatus.ACTIVE;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    // --- APPLICATION-LEVEL LOGICAL KEYS ---
    @Column(name = "class_group_id")
    Long classGroupId;

    @Column(name = "major_id")
    Long majorId;

    @Column(name = "user_id")
    String userId;
}
