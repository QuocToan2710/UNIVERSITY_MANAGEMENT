package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordPaymentRequest {
    @NotNull(message = "Mã sinh viên không được để trống")
    Long studentId;

    String semester;
    String academicYear;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @Min(value = 1000, message = "Số tiền thanh toán tối thiểu 1.000 VNĐ")
    Long paymentAmount;

    Long discountAmount;
    LocalDate dueDate;
    String paymentMethod;
    String transactionReference;
    String note;
}