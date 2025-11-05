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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String courseCode;
    String courseName;
    int credit;
    String semester;

    @ManyToOne
    Teacher teacher;

    @ManyToMany
    List<Student> students;

}
