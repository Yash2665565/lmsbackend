package com.school.diary.repository;

import com.school.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findBySectionIdOrderByCreatedAtDesc(Long sectionId);

    /** Enriched diary entries for a section (subject + teacher names). */
    @Query(value = "SELECT d.id AS id, d.title AS title, d.description AS description, d.due_date AS dueDate, " +
                   "d.subject_id AS subjectId, t.name AS subjectName, d.section_id AS sectionId, " +
                   "d.teacher_id AS teacherId, u.name AS teacherName, d.created_at AS createdAt " +
                   "FROM diary d " +
                   "LEFT JOIN topics t   ON t.id = d.subject_id " +
                   "LEFT JOIN teachers te ON te.id = d.teacher_id " +
                   "LEFT JOIN users u    ON u.id = te.user_id " +
                   "WHERE d.section_id = :sid ORDER BY d.created_at DESC", nativeQuery = true)
    List<DiaryView> findEnrichedBySection(@Param("sid") Long sid);

    /** Sections where this teacher is the class teacher. */
    @Query(value = "SELECT s.id AS sectionId, CONCAT(cg.name, ' - ', s.name) AS label " +
                   "FROM sections s JOIN class_grades cg ON cg.id = s.class_grade_id " +
                   "WHERE s.class_teacher_id = :tid ORDER BY cg.name, s.name", nativeQuery = true)
    List<SectionLite> findSectionsForClassTeacher(@Param("tid") Long tid);

    /** All sections with their current class teacher (admin assignment view). */
    @Query(value = "SELECT s.id AS sectionId, CONCAT(cg.name, ' - ', s.name) AS label, " +
                   "s.class_teacher_id AS classTeacherId, u.name AS teacherName " +
                   "FROM sections s JOIN class_grades cg ON cg.id = s.class_grade_id " +
                   "LEFT JOIN teachers te ON te.id = s.class_teacher_id " +
                   "LEFT JOIN users u    ON u.id = te.user_id " +
                   "ORDER BY cg.name, s.name", nativeQuery = true)
    List<SectionTeacherRow> findAllSectionsWithTeacher();

    @Query(value = "SELECT class_teacher_id FROM sections WHERE id = :sid", nativeQuery = true)
    Long findClassTeacherId(@Param("sid") Long sid);

    /** (section + subject) pairs this teacher is allocated to teach. */
    @Query(value = "SELECT st.section_id AS sectionId, CONCAT(cg.name, ' - ', sec.name) AS sectionLabel, " +
                   "st.topic_id AS topicId, tp.name AS subjectName " +
                   "FROM subject_teachers st " +
                   "JOIN sections sec ON sec.id = st.section_id " +
                   "JOIN class_grades cg ON cg.id = sec.class_grade_id " +
                   "JOIN topics tp ON tp.id = st.topic_id " +
                   "WHERE st.teacher_id = :tid ORDER BY cg.name, sec.name, tp.name", nativeQuery = true)
    List<AllocationRow> findAllocationsForTeacher(@Param("tid") Long tid);

    /** Whether a teacher teaches any subject in a section (for posting rights). */
    @Query(value = "SELECT COUNT(*) FROM subject_teachers WHERE teacher_id = :tid AND section_id = :sid", nativeQuery = true)
    long countTeacherInSection(@Param("tid") Long tid, @Param("sid") Long sid);

    interface AllocationRow {
        Long getSectionId();
        String getSectionLabel();
        Long getTopicId();
        String getSubjectName();
    }

    @Query(value = "SELECT section_id FROM enrollments WHERE student_id = :sid ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Long findSectionIdByStudent(@Param("sid") Long sid);

    @Modifying
    @Query(value = "UPDATE sections SET class_teacher_id = :tid WHERE id = :sid", nativeQuery = true)
    void assignClassTeacher(@Param("sid") Long sid, @Param("tid") Long tid);

    interface DiaryView {
        Long getId();
        String getTitle();
        String getDescription();
        LocalDate getDueDate();
        Long getSubjectId();
        String getSubjectName();
        Long getSectionId();
        Long getTeacherId();
        String getTeacherName();
        LocalDateTime getCreatedAt();
    }

    interface SectionLite {
        Long getSectionId();
        String getLabel();
    }

    interface SectionTeacherRow {
        Long getSectionId();
        String getLabel();
        Long getClassTeacherId();
        String getTeacherName();
    }
}
