package com.toan.university_management.dto.reports;

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
    private Date dob;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
}
