package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "class_group", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_class_code_deleted", columnNames = {"class_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_class_group_major", columnList = "major_id"),
        @Index(name = "idx_class_group_teacher", columnList = "homeroom_teacher_id")
    }
)
@SQLDelete(sql = "UPDATE class_group SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class ClassGroup extends BaseEntity {

    @Column(name = "class_code", nullable = false)
    String classCode;

    @Column(name = "class_name", nullable = false)
    String className;

    @Column(name = "major_id")
    Long majorId;

    @Column(name = "academic_year")
    String academicYear;

    @Column(name = "homeroom_teacher_id")
    Long homeroomTeacherId;

    @Builder.Default
    @Column(name = "max_students")
    Integer maxStudents = 50;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
