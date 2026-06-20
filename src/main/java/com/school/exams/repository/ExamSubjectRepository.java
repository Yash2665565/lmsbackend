package com.school.exams.repository;

import com.school.exams.entity.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {
    List<ExamSubject> findByExamId(Long examId);
    List<ExamSubject> findByExamIdAndClassGradeId(Long examId, Long classGradeId);
}
