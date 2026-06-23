package com.school.lms.repository;

import com.school.lms.entity.CourseNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseNoteRepository extends JpaRepository<CourseNote, Long> {
    List<CourseNote> findByUnitIdOrderByCreatedAtAsc(Long unitId);
}
