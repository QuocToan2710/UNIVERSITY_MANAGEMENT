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
@Table(name = "subject",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_subject_code_deleted", columnNames = {"subject_code", "deleted_key"})
    }
)
@SQLDelete(sql = "UPDATE subject SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Subject extends BaseEntity {

    @Column(name = "subject_code", nullable = false)
    String subjectCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "credit", nullable = false)
    int credit;

    @Builder.Default
    @Column(name = "attendance_coeff")
    Integer attendanceCoeff = 1;

    @Builder.Default
    @Column(name = "midterm_coeff")
    Integer midtermCoeff = 3;

    @Builder.Default
    @Column(name = "final_coeff")
    Integer finalCoeff = 6;

    public int getAttendanceCoeff() {
        return attendanceCoeff != null && attendanceCoeff > 0 ? attendanceCoeff : 1;
    }

    public int getMidtermCoeff() {
        return midtermCoeff != null && midtermCoeff > 0 ? midtermCoeff : 3;
    }

    public int getFinalCoeff() {
        return finalCoeff != null && finalCoeff > 0 ? finalCoeff : 6;
    }

    @Column(name = "description")
    String description;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
