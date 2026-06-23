package com.school.lms.repository;

import com.school.lms.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findBySubjectIdOrderByOrderNoAsc(Long subjectId);
}
