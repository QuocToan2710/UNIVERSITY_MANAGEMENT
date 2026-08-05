package com.toan.university_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String studentCode;
    String fullName;
    String email;
    String phoneNumber;
    Date dob;
    String gender;
    String address;

    @ManyToMany
    List<Course> courses;
}
