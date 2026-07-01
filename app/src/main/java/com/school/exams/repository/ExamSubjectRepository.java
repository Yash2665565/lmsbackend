package com.school.exams.repository;

import com.school.exams.entity.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {
    List<ExamSubject> findByExamId(Long examId);
    List<ExamSubject> findByExamIdAndClassGradeId(Long examId, Long classGradeId);
    List<ExamSubject> findByClassGradeIdOrderByExamDateAsc(Long classGradeId);

    /** Subjects mapped to a class via class_subjects (for the datesheet dropdown). */
    @Query(value = "SELECT t.id AS id, t.name AS name " +
                   "FROM class_subjects cs JOIN topics t ON t.id = cs.topic_id " +
                   "WHERE cs.class_grade_id = :cg ORDER BY t.name", nativeQuery = true)
    List<SubjectLite> findClassSubjects(@Param("cg") Long classGradeId);

    /** A student's current class-grade, resolved via enrollment → section. */
    @Query(value = "SELECT sec.class_grade_id FROM enrollments e " +
                   "JOIN sections sec ON sec.id = e.section_id " +
                   "WHERE e.student_id = :sid ORDER BY e.id DESC LIMIT 1", nativeQuery = true)
    Long findClassGradeByStudent(@Param("sid") Long sid);

    interface SubjectLite {
        Long getId();
        String getName();
    }
}
