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
@Table(name = "floor",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_floor_code_deleted", columnNames = {"floor_code", "deleted"})
    },
    indexes = {
        @Index(name = "idx_floor_building", columnList = "building_id")
    }
)
@SQLDelete(sql = "UPDATE floor SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "floor_code", nullable = false)
    String floorCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "building_id")
    Long buildingId;

    @Column(name = "floor_number")
    Integer floorNumber;

    @Column(name = "status", nullable = false)
    String status; // ACTIVE, MAINTENANCE, INACTIVE

    @Column(name = "description")
    String description;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;
}
