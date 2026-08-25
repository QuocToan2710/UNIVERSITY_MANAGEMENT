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
@Table(name = "department",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_department_code_deleted", columnNames = {"department_code", "deleted_key"})
    }
)
@SQLDelete(sql = "UPDATE department SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Department extends BaseEntity {

    @Column(name = "department_code", nullable = false)
    String departmentCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
