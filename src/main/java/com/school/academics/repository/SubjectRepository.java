package com.school.academics.repository;

import com.school.academics.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByIsHiddenFalseOrderByPosition();

    @Query("SELECT DISTINCT st.subject FROM SubjectTeacher st WHERE st.teacher.user.id = :userId AND st.academicYear.isCurrent = true")
    List<Subject> findSubjectsByTeacherUserId(Long userId);
}
