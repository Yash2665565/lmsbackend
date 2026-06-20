package com.school.academics.repository;

import com.school.academics.entity.ClassGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassGradeRepository extends JpaRepository<ClassGrade, Long> {
}
