package com.school.fees.repository;

import com.school.fees.entity.StudentFee;
import com.school.fees.dto.StudentLite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {

    List<StudentFee> findByStudentId(Long studentId);
    Optional<StudentFee> findByStudentIdAndFeeStructureId(Long studentId, Long feeStructureId);

    /* ── Enrollment lookups (native — students map to sections via enrollments) ── */

    @Query(value = "SELECT section_id FROM enrollments WHERE student_id = :sid ORDER BY id DESC LIMIT 1",
           nativeQuery = true)
    Long findSectionIdByStudent(@Param("sid") Long sid);

    @Query(value = "SELECT s.id AS studentId, " +
                   "TRIM(CONCAT(COALESCE(s.first_name,''),' ',COALESCE(s.last_name,''))) AS studentName, " +
                   "s.admission_no AS admissionNo " +
                   "FROM enrollments e JOIN students s ON s.id = e.student_id " +
                   "WHERE e.section_id = :secId ORDER BY studentName",
           nativeQuery = true)
    List<StudentLite> findStudentsBySection(@Param("secId") Long secId);
}
