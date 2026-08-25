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
@Table(name = "ward",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_ward_code_deleted", columnNames = {"ward_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_ward_district", columnList = "district_id")
    }
)
@SQLDelete(sql = "UPDATE ward SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Ward extends BaseEntity {

    @Column(name = "ward_code", nullable = false)
    String wardCode;

    @Column(name = "ward_name", nullable = false)
    String wardName;

    @Column(name = "ward_type")
    String wardType; // "Phường", "Xã", "Thị trấn"

    @Column(name = "district_id", nullable = false)
    Long districtId;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
