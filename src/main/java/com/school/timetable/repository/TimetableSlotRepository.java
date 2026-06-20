package com.school.timetable.repository;

import com.school.timetable.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findBySectionIdAndAcademicYearId(Long sectionId, Long academicYearId);

    @Query("SELECT ts FROM TimetableSlot ts WHERE ts.section.id IN " +
           "(SELECT e.section.id FROM Enrollment e WHERE e.student.id = :studentId AND e.academicYear.isCurrent = true) " +
           "AND ts.academicYear.isCurrent = true ORDER BY ts.dayOfWeek, ts.period.sortOrder")
    List<TimetableSlot> findByStudentId(Long studentId);
}
