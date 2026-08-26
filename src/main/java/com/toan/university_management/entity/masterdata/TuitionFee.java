package com.toan.university_management.entity.masterdata;

import com.toan.university_management.common.entity.BaseEntity;
import com.toan.university_management.enums.TuitionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tuition_fee",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tuition_student_semester_deleted", columnNames = {"student_id", "semester", "academic_year", "deleted_key"})
    },
    indexes = {
        @Index(name = "idx_tuition_student", columnList = "student_id"),
        @Index(name = "idx_tuition_semester_year", columnList = "semester, academic_year")
    }
)
@SQLDelete(sql = "UPDATE tuition_fee SET deleted = true, deleted_key = CAST(id AS CHAR) WHERE id = ?")
@SQLRestriction("deleted = false")
public class TuitionFee extends BaseEntity {

    @Column(name = "student_id", nullable = false)
    Long studentId;

    @Column(name = "semester", nullable = false, length = 20)
    String semester;

    @Column(name = "academic_year", nullable = false, length = 30)
    String academicYear;

    @Column(name = "total_credits")
    @Builder.Default
    Integer totalCredits = 0;

    @Column(name = "price_per_credit")
    @Builder.Default
    Long pricePerCredit = 450000L;

    @Column(name = "total_amount")
    @Builder.Default
    Long totalAmount = 0L;

    @Column(name = "discount_amount")
    @Builder.Default
    Long discountAmount = 0L;

    @Column(name = "paid_amount")
    @Builder.Default
    Long paidAmount = 0L;

    @Column(name = "balance_amount")
    @Builder.Default
    Long balanceAmount = 0L;

    @Column(name = "due_date")
    LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    TuitionStatus status = TuitionStatus.UNPAID;

    @Column(name = "notes", length = 500)
    String notes;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    @Builder.Default
    @Column(name = "deleted_key", nullable = false, length = 64)
    String deletedKey = "";

    @PrePersist
    @PreUpdate
    public void calculateAmounts() {
        if (pricePerCredit == null) pricePerCredit = 450000L;
        if (totalCredits != null && totalCredits > 0) {
            this.totalAmount = totalCredits * pricePerCredit;
        }
        if (totalAmount == null) totalAmount = 0L;
        if (discountAmount == null) discountAmount = 0L;
        if (paidAmount == null) paidAmount = 0L;

        long netPayable = Math.max(0, totalAmount - discountAmount);
        this.balanceAmount = Math.max(0, netPayable - paidAmount);

        if (balanceAmount <= 0) {
            this.status = TuitionStatus.PAID;
        } else if (paidAmount > 0) {
            this.status = TuitionStatus.PARTIALLY_PAID;
        } else {
            if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
                this.status = TuitionStatus.OVERDUE;
            } else {
                this.status = TuitionStatus.UNPAID;
            }
        }
    }
}