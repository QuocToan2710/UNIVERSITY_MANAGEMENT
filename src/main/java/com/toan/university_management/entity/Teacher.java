package com.toan.university_management.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String teacherCode;
    String fullName;
    String email;
    String phoneNumber;
    String specialization;

    @OneToMany
    List<Course> courses;
}
