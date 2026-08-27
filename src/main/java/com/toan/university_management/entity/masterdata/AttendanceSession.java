package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import com.toan.university_management.enums.AttendanceSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "attendance_session",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_session_code_deleted", columnNames = {"session_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_att_session_subject_class", columnList = "subject_class_id"),
        @Index(name = "idx_att_session_teacher", columnList = "teacher_id"),
        @Index(name = "idx_att_session_date", columnList = "session_date")
    }
)
@SQLDelete(sql = "UPDATE attendance_session SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class AttendanceSession extends BaseEntity {

    @Column(name = "session_code", nullable = false)
    String sessionCode;

    @Column(name = "name")
    String name;

    @Column(name = "subject_class_id", nullable = false)
    Long subjectClassId;

    @Column(name = "class_schedule_id")
    Long classScheduleId;

    @Column(name = "teacher_id")
    Long teacherId;

    @Column(name = "session_number", nullable = false)
    Integer sessionNumber;

    @Column(name = "session_date", nullable = false)
    LocalDate sessionDate;

    @Builder.Default
    @Column(name = "lesson_count", nullable = false)
    Integer lessonCount = 3;

    @Column(name = "room")
    String room;

    @Column(name = "topic")
    String topic;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    AttendanceSessionStatus status = AttendanceSessionStatus.PENDING;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
