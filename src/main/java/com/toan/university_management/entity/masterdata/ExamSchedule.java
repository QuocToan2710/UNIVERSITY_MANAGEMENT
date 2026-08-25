package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "exam_schedule", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_exam_code_deleted", columnNames = {"exam_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_exam_schedule_subject_class", columnList = "subject_class_id"),
        @Index(name = "idx_exam_schedule_subject", columnList = "subject_id"),
        @Index(name = "idx_exam_schedule_proctor", columnList = "proctor_id")
    }
)
@SQLDelete(sql = "UPDATE exam_schedule SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class ExamSchedule extends BaseEntity {

    @Column(name = "exam_code", nullable = false)
    String examCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "subject_class_id")
    Long subjectClassId;

    @Column(name = "subject_id")
    Long subjectId;

    @Column(name = "exam_date")
    LocalDate examDate;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "room")
    String room;

    @Column(name = "exam_format")
    String examFormat;

    @Column(name = "proctor_id")
    Long proctorId;

    @Column(name = "proctor_name")
    String proctorName;

    @Column(name = "semester")
    String semester;

    @Column(name = "academic_year")
    String academicYear;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
