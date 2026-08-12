package com.toan.university_management.entity.masterdata;

import com.toan.university_management.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "enrollment", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment_code_deleted", columnNames = {"enrollment_code", "deleted"}),
        @UniqueConstraint(name = "uk_enrollment_student_class_deleted", columnNames = {"student_id", "subject_class_id", "deleted"})
    },
    indexes = {
        @Index(name = "idx_enrollment_student", columnList = "student_id"),
        @Index(name = "idx_enrollment_subject_class", columnList = "subject_class_id")
    }
)
@SQLDelete(sql = "UPDATE enrollment SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "enrollment_code", nullable = false)
    String enrollmentCode;

    @Column(name = "name")
    String name;

    @Column(name = "student_id", nullable = false)
    Long studentId;

    @Column(name = "subject_class_id", nullable = false)
    Long subjectClassId;

    @Column(name = "midterm_score")
    Double midtermScore;

    @Column(name = "final_score")
    Double finalScore;

    @Column(name = "total_score")
    Double totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    EnrollmentStatus status = EnrollmentStatus.REGISTERED;

    @Column(name = "enrolled_at")
    LocalDateTime enrolledAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;
}
