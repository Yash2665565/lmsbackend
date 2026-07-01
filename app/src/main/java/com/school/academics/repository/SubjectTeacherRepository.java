package com.school.academics.repository;

import com.school.academics.entity.SubjectTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectTeacherRepository extends JpaRepository<SubjectTeacher, Long> {

    Optional<SubjectTeacher> findBySection_IdAndSubject_Id(Long sectionId, Long subjectId);

    /** Subjects of a section's class, with the assigned teacher (if any). */
    @Query(value = "SELECT cs.topic_id AS topicId, t.name AS subjectName, " +
                   "st.id AS assignmentId, st.teacher_id AS teacherId, u.name AS teacherName " +
                   "FROM sections sec " +
                   "JOIN class_subjects cs ON cs.class_grade_id = sec.class_grade_id " +
                   "JOIN topics t ON t.id = cs.topic_id " +
                   "LEFT JOIN subject_teachers st ON st.topic_id = cs.topic_id AND st.section_id = sec.id " +
                   "LEFT JOIN teachers te ON te.id = st.teacher_id " +
                   "LEFT JOIN users u ON u.id = te.user_id " +
                   "WHERE sec.id = :sid ORDER BY t.name", nativeQuery = true)
    List<SubjectRow> findSectionSubjects(@Param("sid") Long sectionId);

    /** Everything a teacher teaches: which section + which subject. */
    @Query(value = "SELECT st.id AS assignmentId, st.section_id AS sectionId, " +
                   "CONCAT(cg.name, ' - ', sec.name) AS sectionLabel, " +
                   "st.topic_id AS topicId, tp.name AS subjectName " +
                   "FROM subject_teachers st " +
                   "JOIN sections sec ON sec.id = st.section_id " +
                   "JOIN class_grades cg ON cg.id = sec.class_grade_id " +
                   "JOIN topics tp ON tp.id = st.topic_id " +
                   "WHERE st.teacher_id = :tid ORDER BY cg.name, sec.name, tp.name", nativeQuery = true)
    List<TeachingRow> findTeaching(@Param("tid") Long teacherId);

    /** Sections where the teacher is the class teacher. */
    @Query(value = "SELECT CONCAT(cg.name, ' - ', sec.name) AS label " +
                   "FROM sections sec JOIN class_grades cg ON cg.id = sec.class_grade_id " +
                   "WHERE sec.class_teacher_id = :tid ORDER BY cg.name, sec.name", nativeQuery = true)
    List<String> findClassTeacherOf(@Param("tid") Long teacherId);

    interface SubjectRow {
        Long getTopicId();
        String getSubjectName();
        Long getAssignmentId();
        Long getTeacherId();
        String getTeacherName();
    }

    interface TeachingRow {
        Long getAssignmentId();
        Long getSectionId();
        String getSectionLabel();
        Long getTopicId();
        String getSubjectName();
    }
}
