package com.school.exams.repository;

import com.school.exams.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByStudentIdAndExamSubjectId(Long studentId, Long examSubjectId);
    List<Mark> findByStudentId(Long studentId);
    List<Mark> findByExamSubjectId(Long examSubjectId);

    @Query("SELECT m FROM Mark m WHERE m.student.id = :studentId AND m.examSubject.exam.term.id = :termId")
    List<Mark> findByStudentAndTerm(Long studentId, Long termId);
}
