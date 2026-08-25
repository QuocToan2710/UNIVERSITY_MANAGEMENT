package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import com.toan.university_management.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "student", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_code_deleted", columnNames = {"student_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_student_class_group", columnList = "class_group_id"),
        @Index(name = "idx_student_major", columnList = "major_id"),
        @Index(name = "idx_student_user", columnList = "user_id")
    }
)
@SQLDelete(sql = "UPDATE student SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Student extends BaseEntity {

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

    @Column(name = "province_id")
    Long provinceId;

    @Column(name = "district_id")
    Long districtId;

    @Column(name = "ward_id")
    Long wardId;

    @Column(name = "specific_address")
    String specificAddress;

    @Column(name = "enrollment_year")
    String enrollmentYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    StudentStatus status = StudentStatus.ACTIVE;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";

    // --- APPLICATION-LEVEL LOGICAL KEYS ---
    @Column(name = "class_group_id")
    Long classGroupId;

    @Column(name = "major_id")
    Long majorId;

    @Column(name = "user_id")
    Long userId;
}
