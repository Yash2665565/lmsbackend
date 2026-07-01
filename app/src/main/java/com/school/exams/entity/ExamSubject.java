package com.school.exams.entity;

import com.school.academics.entity.ClassGrade;
import com.school.academics.entity.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_subjects")
@Getter @Setter
public class ExamSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_grade_id", nullable = false)
    private ClassGrade classGrade;

    @Column(name = "max_marks")
    private int maxMarks = 100;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
