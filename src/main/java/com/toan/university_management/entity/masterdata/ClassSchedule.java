package com.toan.university_management.entity.masterdata;

import com.toan.university_management.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "class_schedule", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_schedule_code_deleted", columnNames = {"schedule_code", "deleted"})
    },
    indexes = {
        @Index(name = "idx_schedule_subject_class", columnList = "subject_class_id"),
        @Index(name = "idx_schedule_teacher", columnList = "teacher_id")
    }
)
@SQLDelete(sql = "UPDATE class_schedule SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "schedule_code", nullable = false)
    String scheduleCode;

    @Column(name = "name")
    String name;

    @Column(name = "subject_class_id", nullable = false)
    Long subjectClassId;

    @Column(name = "teacher_id")
    Long teacherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    WeekDay dayOfWeek;

    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalTime endTime;

    @Column(name = "room")
    String room;

    @Column(name = "semester")
    String semester;

    @Column(name = "academic_year")
    String academicYear;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;
}
