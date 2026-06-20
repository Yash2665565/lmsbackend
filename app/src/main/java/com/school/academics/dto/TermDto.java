package com.school.academics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermDto {
    private Long id;
    private Long academicYearId;
    private String academicYearName;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
