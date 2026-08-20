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
@Table(name = "subject",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_subject_code_deleted", columnNames = {"subject_code", "deleted_key"})
    }
)
@SQLDelete(sql = "UPDATE subject SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "subject_code", nullable = false)
    String subjectCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "credit", nullable = false)
    int credit;

    @Column(name = "description")
    String description;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
