package com.toan.university_management.entity.masterdata;

import com.toan.university_management.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "class_schedule")
@SQLDelete(sql = "UPDATE class_schedule SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id", nullable = false)
    ClassGroup classGroup;

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
    String semester;                // VD: HK1, HK2

    @Column(name = "academic_year")
    String academicYear;            // VD: 2025-2026

    @Column(name = "note", columnDefinition = "TEXT")
    String note;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;
}

