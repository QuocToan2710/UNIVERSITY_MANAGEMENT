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
@Table(name = "district",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_district_code_deleted", columnNames = {"district_code", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_district_province", columnList = "province_id")
    }
)
@SQLDelete(sql = "UPDATE district SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class District extends BaseEntity {

    @Column(name = "district_code", nullable = false)
    String districtCode;

    @Column(name = "district_name", nullable = false)
    String districtName;

    @Column(name = "district_type")
    String districtType; // "Quận", "Huyện", "Thị xã", "Thành phố thuộc tỉnh", "Thành phố thuộc thành phố trực thuộc Trung ương"

    @Column(name = "province_id", nullable = false)
    Long provinceId;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
