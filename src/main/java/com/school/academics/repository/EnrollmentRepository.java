package com.school.academics.repository;

import com.school.academics.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findBySectionIdAndAcademicYearId(Long sectionId, Long academicYearId);
    Optional<Enrollment> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.user.id = :userId AND e.academicYear.isCurrent = true")
    Optional<Enrollment> findCurrentEnrollmentByUserId(Long userId);
}
