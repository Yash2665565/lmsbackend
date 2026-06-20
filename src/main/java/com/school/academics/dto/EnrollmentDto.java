package com.school.academics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNo;
    private Long sectionId;
    private String sectionName;
    private String className;
    private Long academicYearId;
    private Integer rollNo;
    private String status;
}
