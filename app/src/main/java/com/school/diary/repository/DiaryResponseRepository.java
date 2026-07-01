package com.school.diary.repository;

import com.school.diary.entity.DiaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryResponseRepository extends JpaRepository<DiaryResponse, Long> {

    Optional<DiaryResponse> findByDiaryIdAndStudentId(Long diaryId, Long studentId);

    List<DiaryResponse> findByStudentId(Long studentId);

    List<DiaryResponse> findByDiaryIdIn(List<Long> diaryIds);

    /** Current-year roster of a section (student + roll), for the teacher response report. */
    @Query(value = "SELECT e.student_id AS studentId, " +
                   "TRIM(CONCAT(COALESCE(s.first_name,''),' ',COALESCE(s.last_name,''))) AS studentName, " +
                   "e.roll_no AS rollNo " +
                   "FROM enrollments e JOIN students s ON s.id = e.student_id " +
                   "WHERE e.section_id = :sid " +
                   "AND e.academic_year_id = (SELECT MAX(academic_year_id) FROM enrollments WHERE section_id = :sid) " +
                   "ORDER BY e.roll_no, studentName", nativeQuery = true)
    List<RosterRow> findRoster(@Param("sid") Long sid);

    interface RosterRow {
        Long getStudentId();
        String getStudentName();
        Integer getRollNo();
    }
}
