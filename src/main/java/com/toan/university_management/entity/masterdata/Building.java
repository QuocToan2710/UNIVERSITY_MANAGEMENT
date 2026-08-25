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
@Table(name = "building",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_building_code_deleted", columnNames = {"building_code", "deleted_key"})
    }
)
@SQLDelete(sql = "UPDATE building SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Building extends BaseEntity {

    @Column(name = "building_code", nullable = false)
    String buildingCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "total_floors")
    Integer totalFloors;

    @Column(name = "status", nullable = false)
    String status; // ACTIVE, MAINTENANCE, INACTIVE

    @Column(name = "description")
    String description;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
