package com.school.academics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long sectionId;

    @NotNull
    private Long academicYearId;

    private Integer rollNo;
}
