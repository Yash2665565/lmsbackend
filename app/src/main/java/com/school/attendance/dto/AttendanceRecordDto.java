package com.school.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNo;
    private String status;
    private LocalDate date;
    private Long periodId;
    private String remarks;
}
