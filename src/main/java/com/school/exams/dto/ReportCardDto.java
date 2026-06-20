package com.school.exams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardDto {

    private Long studentId;
    private String studentName;
    private String admissionNo;
    private String className;
    private String sectionName;
    private Long termId;
    private String termName;
    private BigDecimal attendancePct;
    private List<SubjectMarkDto> subjects;
    private BigDecimal totalPct;
    private BigDecimal gpa;
    private String classTeacherRemarks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectMarkDto {
        private String subjectName;
        private int maxMarks;
        private BigDecimal marksObtained;
        private String grade;
        private BigDecimal gradePoint;
    }
}
