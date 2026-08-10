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
@Table(name = "course", uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_code_deleted", columnNames = {"course_code", "deleted"})
})
@SQLDelete(sql = "UPDATE course SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "course_code")
    String courseCode;

    @Column(name = "course_name")
    String courseName;

    @Column(name = "credit")
    int credit;

    @Column(name = "semester")
    String semester;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;         // Mô tả môn học

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @ManyToMany
    List<Student> students;

}

