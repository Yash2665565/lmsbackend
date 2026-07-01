package com.school.lms.repository;

import com.school.lms.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Read-only queries that scope LMS "courses" (subjects/topics) to a role's context.
 * Reuses the Unit entity only to satisfy JpaRepository's type param; all methods are native projections.
 */
public interface LmsCourseRepository extends JpaRepository<Unit, Long> {

    /** Subjects of the student's class (via their enrollment → section → class → class_subjects). */
    @Query(value = "SELECT DISTINCT tp.id AS id, tp.name AS name, tp.code AS code, tp.description AS description " +
                   "FROM enrollments e " +
                   "JOIN sections sec ON sec.id = e.section_id " +
                   "JOIN class_subjects cs ON cs.class_grade_id = sec.class_grade_id " +
                   "JOIN topics tp ON tp.id = cs.topic_id " +
                   "WHERE e.student_id = :studentId ORDER BY tp.name", nativeQuery = true)
    List<CourseRow> studentSubjects(@Param("studentId") Long studentId);

    /** Subjects this teacher is allocated to teach in a specific section. */
    @Query(value = "SELECT DISTINCT tp.id AS id, tp.name AS name, tp.code AS code, tp.description AS description " +
                   "FROM subject_teachers st JOIN topics tp ON tp.id = st.topic_id " +
                   "WHERE st.teacher_id = :teacherId AND st.section_id = :sectionId ORDER BY tp.name", nativeQuery = true)
    List<CourseRow> teacherSubjectsInSection(@Param("teacherId") Long teacherId, @Param("sectionId") Long sectionId);

    /** All distinct subjects this teacher teaches across every section (dashboard / no filter). */
    @Query(value = "SELECT DISTINCT tp.id AS id, tp.name AS name, tp.code AS code, tp.description AS description " +
                   "FROM subject_teachers st JOIN topics tp ON tp.id = st.topic_id " +
                   "WHERE st.teacher_id = :teacherId ORDER BY tp.name", nativeQuery = true)
    List<CourseRow> teacherAllSubjects(@Param("teacherId") Long teacherId);

    /** Subjects mapped to the class of a given section (admin view of a section). */
    @Query(value = "SELECT DISTINCT tp.id AS id, tp.name AS name, tp.code AS code, tp.description AS description " +
                   "FROM sections sec JOIN class_subjects cs ON cs.class_grade_id = sec.class_grade_id " +
                   "JOIN topics tp ON tp.id = cs.topic_id " +
                   "WHERE sec.id = :sectionId ORDER BY tp.name", nativeQuery = true)
    List<CourseRow> classSubjectsOfSection(@Param("sectionId") Long sectionId);

    /** Distinct (class, section) pairs this teacher is allocated to teach — for scoping the LMS filter. */
    @Query(value = "SELECT DISTINCT sec.id AS sectionId, sec.name AS sectionName, " +
                   "cg.id AS classId, cg.name AS className " +
                   "FROM subject_teachers st " +
                   "JOIN sections sec ON sec.id = st.section_id " +
                   "JOIN class_grades cg ON cg.id = sec.class_grade_id " +
                   "WHERE st.teacher_id = :teacherId ORDER BY cg.name, sec.name", nativeQuery = true)
    List<MySectionRow> teacherSections(@Param("teacherId") Long teacherId);

    interface CourseRow {
        Long getId();
        String getName();
        String getCode();
        String getDescription();
    }

    interface MySectionRow {
        Long getSectionId();
        String getSectionName();
        Long getClassId();
        String getClassName();
    }
}
