package com.school.lms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class AssignmentDto {
    private Long id;
    private Long unitId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private int maxMarks;
    private LocalDateTime createdAt;
    // populated when fetching for a specific student
    private AssignmentSubmissionDto mySubmission;
}
