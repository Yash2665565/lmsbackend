package com.school.lms.repository;

import com.school.lms.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByUnitIdOrderByDueDateAsc(Long unitId);
    List<Assignment> findByUnitIdIn(List<Long> unitIds);
}
