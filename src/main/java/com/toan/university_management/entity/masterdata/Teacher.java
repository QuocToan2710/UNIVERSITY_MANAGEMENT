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
        @UniqueConstraint(name = "uk_teacher_code_deleted", columnNames = {"teacher_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_teacher_department", columnList = "department_id")
    }
)
@SQLDelete(sql = "UPDATE teacher SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
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

    @Column(name = "department_id")
    Long departmentId;

    @Column(name = "user_id")
    String userId;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
