package com.school.attendance.repository;

import com.school.attendance.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, AttendanceSummary.AttendanceSummaryId> {

    Optional<AttendanceSummary> findByStudentIdAndTermId(Long studentId, Long termId);

    @Query("SELECT s FROM AttendanceSummary s WHERE s.term.id = :termId AND s.attendancePct < :threshold")
    List<AttendanceSummary> findLowAttendance(Long termId, BigDecimal threshold);

    @Query("SELECT s FROM AttendanceSummary s WHERE s.student.id = :studentId")
    List<AttendanceSummary> findByStudentId(Long studentId);
}
