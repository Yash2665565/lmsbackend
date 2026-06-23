package com.school.lms.dto;

import com.school.lms.entity.AssignmentSubmission.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class AssignmentSubmissionDto {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String content;
    private LocalDateTime submittedAt;
    private BigDecimal marksObtained;
    private String feedback;
    private SubmissionStatus status;
}
