package com.toan.university_management.entity.masterdata;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "class_group", uniqueConstraints = {
    @UniqueConstraint(name = "uk_class_code_deleted", columnNames = {"class_code", "deleted"})
})
@SQLDelete(sql = "UPDATE class_group SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "class_code", nullable = false)
    String classCode;

    @Column(name = "class_name", nullable = false)
    String className;

    @Column(name = "major")
    String major;                   // Ngành học, VD: Công nghệ thông tin

    @Column(name = "academic_year")
    String academicYear;            // Niên khóa, VD: 2023-2027

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeroom_teacher_id")
    Teacher homeroomTeacher;        // Giáo viên chủ nhiệm

    @Builder.Default
    @OneToMany(mappedBy = "classGroup", fetch = FetchType.LAZY)
    List<Student> students = new ArrayList<>();
}

