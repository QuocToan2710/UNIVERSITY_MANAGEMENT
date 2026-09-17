package com.toan.university_management.model.reports;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toan.university_management.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentReportDTO {
    private String id;
    private String studentCode;
    private String fullName;
    @JsonFormat(pattern = AppConstants.DATE_FORMAT, timezone = "Asia/Ho_Chi_Minh")
    private Date dob;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
}
