package com.toan.university_management.entity.masterdata;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "teacher", uniqueConstraints = {
    @UniqueConstraint(name = "uk_teacher_code_deleted", columnNames = {"teacher_code", "deleted"})
})
@SQLDelete(sql = "UPDATE teacher SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "teacher_code")
    String teacherCode;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "specialization")
    String specialization;

    @Column(name = "degree")
    String degree;              // Học vị: ThS, TS, GS, PGS

    @Column(name = "department")
    String department;          // Khoa / Bộ môn

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @OneToMany
    List<Course> courses;
}

