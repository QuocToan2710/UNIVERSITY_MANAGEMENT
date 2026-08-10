package com.toan.university_management.entity.masterdata;

import com.toan.university_management.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "student", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_code_deleted", columnNames = {"student_code", "deleted"})
})
@SQLDelete(sql = "UPDATE student SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "student_code")
    String studentCode;

    @Column(name = "full_name")
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

    @Column(name = "major")
    String major;                       // Ngành học

    @Column(name = "enrollment_year")
    String enrollmentYear;              // Năm nhập học, VD: 2023

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    StudentStatus status = StudentStatus.ACTIVE;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    ClassGroup classGroup;              // Lớp học

    @ManyToMany
    List<Course> courses;
}


