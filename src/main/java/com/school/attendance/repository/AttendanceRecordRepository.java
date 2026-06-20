package com.school.attendance.repository;

import com.school.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findBySectionIdAndAttendanceDate(Long sectionId, LocalDate date);

    List<AttendanceRecord> findByStudentIdAndAttendanceDateBetween(Long studentId, LocalDate from, LocalDate to);

    Optional<AttendanceRecord> findByStudentIdAndAttendanceDateAndPeriodId(Long studentId, LocalDate date, Long periodId);

    @Query("SELECT ar.status, COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.student.id = :studentId AND ar.period IS NULL " +
           "AND ar.attendanceDate BETWEEN :from AND :to " +
           "GROUP BY ar.status")
    List<Object[]> countByStatusForStudent(Long studentId, LocalDate from, LocalDate to);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.section.id = :sectionId " +
           "AND ar.attendanceDate = :date AND ar.period IS NULL")
    List<AttendanceRecord> findDailyRegister(Long sectionId, LocalDate date);

    @Query("SELECT DISTINCT ar.student.id FROM AttendanceRecord ar " +
           "WHERE ar.attendanceDate = :date AND ar.status = 'ABSENT' AND ar.period IS NULL")
    List<Long> findAbsentStudentIds(LocalDate date);
}
