package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import com.toan.university_management.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "enrollment", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment_code_deleted", columnNames = {"enrollment_code", "deleted_key"}),
        @UniqueConstraint(name = "uk_enrollment_student_class_deleted", columnNames = {"student_id", "subject_class_id", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_enrollment_student", columnList = "student_id"),
        @Index(name = "idx_enrollment_subject_class", columnList = "subject_class_id")
    }
)
@SQLDelete(sql = "UPDATE enrollment SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Enrollment extends BaseEntity {

    @Column(name = "enrollment_code", nullable = false)
    String enrollmentCode;

    @Column(name = "name")
    String name;

    @Column(name = "student_id", nullable = false)
    Long studentId;

    @Column(name = "subject_class_id", nullable = false)
    Long subjectClassId;

    @Column(name = "attendance_score")
    Double attendanceScore;

    @Column(name = "midterm_score")
    Double midtermScore;

    @Column(name = "final_score")
    Double finalScore;

    @Column(name = "total_score")
    Double totalScore;

    @Column(name = "letter_grade", length = 10)
    String letterGrade;

    @Column(name = "grade_point_4")
    Double gradePoint4;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_status")
    @Builder.Default
    com.toan.university_management.enums.GradeStatus gradeStatus = com.toan.university_management.enums.GradeStatus.DRAFT;

    @Column(name = "note")
    String note;

    @Builder.Default
    @Column(name = "is_appealed")
    Boolean isAppealed = false;

    @Column(name = "total_sessions")
    @Builder.Default
    Integer totalSessions = 0;

    @Column(name = "absent_sessions")
    @Builder.Default
    Integer absentSessions = 0;

    @Column(name = "absence_rate")
    @Builder.Default
    Double absenceRate = 0.0;

    @Builder.Default
    @Column(name = "is_banned_from_exam")
    Boolean isBannedFromExam = false;

    public boolean isAppealed() {
        return Boolean.TRUE.equals(isAppealed);
    }

    public boolean isBannedFromExam() {
        return Boolean.TRUE.equals(isBannedFromExam);
    }

    public com.toan.university_management.enums.GradeStatus getGradeStatus() {
        return gradeStatus != null ? gradeStatus : com.toan.university_management.enums.GradeStatus.DRAFT;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    EnrollmentStatus status = EnrollmentStatus.REGISTERED;

    @Column(name = "enrolled_at")
    LocalDateTime enrolledAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
