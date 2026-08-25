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
@Table(name = "province",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_province_code_deleted", columnNames = {"province_code", "deleted_key"})
    }
)
@SQLDelete(sql = "UPDATE province SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class Province extends BaseEntity {

    @Column(name = "province_code", nullable = false)
    String provinceCode;

    @Column(name = "province_name", nullable = false)
    String provinceName;

    @Column(name = "province_type")
    String provinceType; // "Thành phố Trung ương", "Tỉnh"

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";
}
