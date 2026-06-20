package com.school.exams.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grading_scale")
@Getter @Setter
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_pct")
    private BigDecimal minPct;

    @Column(name = "max_pct")
    private BigDecimal maxPct;

    private String grade;

    @Column(name = "grade_point")
    private BigDecimal gradePoint;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
