package com.toan.university_management.util;

import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.enums.EnrollmentStatus;

public class GradeCalculator {

    private GradeCalculator() {}

    public static void computeAndApplyGrades(Enrollment enrollment, Subject subject) {
        int c1 = (subject != null && subject.getAttendanceCoeff() > 0) ? subject.getAttendanceCoeff() : 1;
        int c2 = (subject != null && subject.getMidtermCoeff() > 0) ? subject.getMidtermCoeff() : 3;
        int c3 = (subject != null && subject.getFinalCoeff() > 0) ? subject.getFinalCoeff() : 6;

        Double att = enrollment.getAttendanceScore();
        Double mid = enrollment.getMidtermScore();
        Double fin = enrollment.getFinalScore();

        if (att == null && mid == null && fin == null) {
            enrollment.setTotalScore(null);
            enrollment.setLetterGrade(null);
            enrollment.setGradePoint4(null);
            enrollment.setStatus(EnrollmentStatus.REGISTERED);
            return;
        }

        if (att != null && att < 4.0) {
            if (enrollment.getNote() == null || enrollment.getNote().isBlank()) {
                enrollment.setNote("Không đủ điều kiện dự thi (Chuyên cần < 4.0)");
            }
        }

        double totalWeighted = 0.0;
        int totalCoeff = 0;

        if (att != null) {
            totalWeighted += att * c1;
            totalCoeff += c1;
        }
        if (mid != null) {
            totalWeighted += mid * c2;
            totalCoeff += c2;
        }
        if (fin != null) {
            totalWeighted += fin * c3;
            totalCoeff += c3;
        }

        if (totalCoeff > 0) {
            double total = Math.round((totalWeighted / totalCoeff) * 100.0) / 100.0;
            enrollment.setTotalScore(total);

            String letter;
            double point4;

            if (total >= 8.5) {
                letter = "A";
                point4 = 4.0;
            } else if (total >= 8.0) {
                letter = "B+";
                point4 = 3.5;
            } else if (total >= 7.0) {
                letter = "B";
                point4 = 3.0;
            } else if (total >= 6.5) {
                letter = "C+";
                point4 = 2.5;
            } else if (total >= 5.5) {
                letter = "C";
                point4 = 2.0;
            } else if (total >= 5.0) {
                letter = "D+";
                point4 = 1.5;
            } else if (total >= 4.0) {
                letter = "D";
                point4 = 1.0;
            } else {
                letter = "F";
                point4 = 0.0;
            }

            enrollment.setLetterGrade(letter);
            enrollment.setGradePoint4(point4);

            if (fin != null) {
                if (total >= 4.0 && (att == null || att >= 4.0)) {
                    enrollment.setStatus(EnrollmentStatus.PASSED);
                } else {
                    enrollment.setStatus(EnrollmentStatus.FAILED);
                }
            } else {
                enrollment.setStatus(EnrollmentStatus.ATTENDING);
            }
        }
    }

    public static String getAcademicRank(Double cpa4) {
        if (cpa4 == null) return "Chưa xếp loại";
        if (cpa4 >= 3.6) return "Xuất sắc";
        if (cpa4 >= 3.2) return "Giỏi";
        if (cpa4 >= 2.5) return "Khá";
        if (cpa4 >= 2.0) return "Trung bình";
        return "Yếu";
    }
}
