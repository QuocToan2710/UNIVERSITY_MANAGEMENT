package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import com.toan.university_management.enums.AttendanceStatus;
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
@Table(name = "attendance_record",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_att_session_student_deleted", columnNames = {"session_id", "student_id", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_att_rec_session", columnList = "session_id"),
        @Index(name = "idx_att_rec_student", columnList = "student_id"),
        @Index(name = "idx_att_rec_enrollment", columnList = "enrollment_id")
    }
)
@SQLDelete(sql = "UPDATE attendance_record SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class AttendanceRecord extends BaseEntity {

    @Column(name = "session_id", nullable = false)
    Long sessionId;

    @Column(name = "enrollment_id", nullable = false)
    Long enrollmentId;

    @Column(name = "student_id", nullable = false)
    Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    AttendanceStatus status = AttendanceStatus.PRESENT;

    @Builder.Default
    @Column(name = "late_minutes")
    Integer lateMinutes = 0;

    @Column(name = "note")
    String note;

    @Column(name = "checked_at")
    LocalDateTime checkedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
