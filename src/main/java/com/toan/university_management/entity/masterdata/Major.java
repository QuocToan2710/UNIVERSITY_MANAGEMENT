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
@Table(name = "major",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_major_code_deleted", columnNames = {"major_code", "deleted"})
    },
    indexes = {
        @Index(name = "idx_major_department", columnList = "department_id")
    }
)
@SQLDelete(sql = "UPDATE major SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "major_code", nullable = false)
    String majorCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "department_id")
    Long departmentId;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;
}
