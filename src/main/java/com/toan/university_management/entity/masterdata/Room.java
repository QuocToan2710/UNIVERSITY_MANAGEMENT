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
@Table(name = "room",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_code_deleted", columnNames = {"room_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_room_building", columnList = "building_id"),
        @Index(name = "idx_room_floor", columnList = "floor_id")
    }
)
@SQLDelete(sql = "UPDATE room SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Room extends BaseEntity {

    @Column(name = "room_code", nullable = false)
    String roomCode;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "building_id")
    Long buildingId;

    @Column(name = "building")
    String building;

    @Column(name = "floor_id")
    Long floorId;

    @Column(name = "floor")
    String floor;

    @Column(name = "capacity")
    Integer capacity;

    @Column(name = "room_type")
    String roomType;

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
