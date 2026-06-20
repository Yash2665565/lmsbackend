package com.school.exams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNo;
    private Long examSubjectId;
    private BigDecimal marksObtained;
    private String grade;
    private int maxMarks;
}
