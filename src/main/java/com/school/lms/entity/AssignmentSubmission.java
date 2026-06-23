package com.school.lms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
@Getter @Setter
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "marks_obtained", precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    public enum SubmissionStatus { SUBMITTED, GRADED, LATE }
}
