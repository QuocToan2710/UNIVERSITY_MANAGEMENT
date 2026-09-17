package com.toan.university_management.model.masterdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toan.university_management.constant.AppConstants;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentSummary {
    String studentCode;
    String fullName;
    String email;
    String phoneNumber;
    @JsonFormat(pattern = AppConstants.DATE_FORMAT, timezone = "Asia/Ho_Chi_Minh")
    Date dob;
    String gender;
    String address;

}
