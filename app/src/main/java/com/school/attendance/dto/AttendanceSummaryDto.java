package com.school.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDto {

    private Long studentId;
    private String studentName;
    private String admissionNo;
    private Long termId;
    private String termName;
    private int presentDays;
    private int absentDays;
    private int lateDays;
    private int totalDays;
    private BigDecimal attendancePct;
}
