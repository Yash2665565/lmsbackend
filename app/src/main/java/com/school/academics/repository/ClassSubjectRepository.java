package com.school.academics.repository;

import com.school.academics.entity.ClassSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {
    Optional<ClassSubject> findByClassGradeIdAndTopicId(Long classGradeId, Long topicId);
}
