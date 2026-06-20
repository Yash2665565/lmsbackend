package com.school.attendance.entity;

import com.school.academics.entity.Term;
import com.school.identity.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_summary")
@Getter @Setter
@IdClass(AttendanceSummary.AttendanceSummaryId.class)
public class AttendanceSummary {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "present_days")
    private int presentDays;

    @Column(name = "absent_days")
    private int absentDays;

    @Column(name = "late_days")
    private int lateDays;

    @Column(name = "total_days")
    private int totalDays;

    @Column(name = "attendance_pct")
    private BigDecimal attendancePct;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Getter @Setter
    public static class AttendanceSummaryId implements Serializable {
        private Long student;
        private Long term;
    }
}
