package com.school.academics.repository;

import com.school.academics.entity.TeacherExpertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherExpertiseRepository extends JpaRepository<TeacherExpertise, Long> {

    List<TeacherExpertise> findByTeacherId(Long teacherId);

    @Modifying
    @Query("delete from TeacherExpertise te where te.teacherId = :tid")
    void deleteByTeacherId(@Param("tid") Long teacherId);

    /** Subjects (topic id + name) a teacher is expert in. */
    @Query(value = "SELECT t.id AS topicId, t.name AS subjectName " +
                   "FROM teacher_expertise te JOIN topics t ON t.id = te.topic_id " +
                   "WHERE te.teacher_id = :tid ORDER BY t.name", nativeQuery = true)
    List<SubjectLite> findSubjectsForTeacher(@Param("tid") Long teacherId);

    /** Teachers (id + name) qualified to teach a subject. */
    @Query(value = "SELECT te.teacher_id AS teacherId, u.name AS teacherName " +
                   "FROM teacher_expertise te JOIN teachers t ON t.id = te.teacher_id " +
                   "JOIN users u ON u.id = t.user_id " +
                   "WHERE te.topic_id = :topicId ORDER BY u.name", nativeQuery = true)
    List<TeacherLite> findTeachersForSubject(@Param("topicId") Long topicId);

    interface SubjectLite { Long getTopicId(); String getSubjectName(); }
    interface TeacherLite { Long getTeacherId(); String getTeacherName(); }
}
