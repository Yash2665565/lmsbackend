package com.school.timetable.repository;

import com.school.timetable.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findBySectionIdAndAcademicYearId(Long sectionId, Long academicYearId);

    List<TimetableSlot> findBySectionIdAndAcademicYearIsCurrent(Long sectionId, boolean isCurrent);

    @Query("SELECT ts FROM TimetableSlot ts WHERE ts.section.id IN " +
           "(SELECT e.section.id FROM Enrollment e WHERE e.student.id = :studentId AND e.academicYear.isCurrent = true) " +
           "AND ts.academicYear.isCurrent = true ORDER BY ts.dayOfWeek, ts.period.sortOrder")
    List<TimetableSlot> findByStudentId(Long studentId);

    @Query("SELECT ts FROM TimetableSlot ts WHERE ts.teacher.id = :teacherId AND ts.academicYear.isCurrent = true ORDER BY ts.dayOfWeek, ts.period.sortOrder")
    List<TimetableSlot> findByTeacherIdAndCurrentYear(@Param("teacherId") Long teacherId);
}
