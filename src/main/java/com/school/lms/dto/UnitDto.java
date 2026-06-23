package com.school.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class UnitDto {
    private Long id;
    private Long subjectId;
    private String title;
    private String description;
    private int orderNo;
    private int notesCount;
    private int assignmentsCount;
    private LocalDateTime createdAt;
}
