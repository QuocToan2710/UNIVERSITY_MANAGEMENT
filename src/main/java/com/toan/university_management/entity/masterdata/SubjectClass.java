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
@Table(name = "subject_class", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_subject_class_code_deleted", columnNames = {"subject_class_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_subject_class_subject", columnList = "subject_id"),
        @Index(name = "idx_subject_class_teacher", columnList = "teacher_id")
    }
)
@SQLDelete(sql = "UPDATE subject_class SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class SubjectClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "subject_class_code", nullable = false)
    String subjectClassCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "subject_id", nullable = false)
    Long subjectId;

    @Column(name = "teacher_id")
    Long teacherId;

    @Column(name = "semester")
    String semester;

    @Column(name = "academic_year")
    String academicYear;

    @Column(name = "max_capacity")
    int maxCapacity;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
