package com.school.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SurpriseTestDto {
    private Long id;
    private Long unitId;
    private String title;
    private String description;
    private LocalDate testDate;
    private int durationMinutes;
    private int maxMarks;
    private LocalDateTime createdAt;
}
