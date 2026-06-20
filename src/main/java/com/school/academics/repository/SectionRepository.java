package com.school.academics.repository;

import com.school.academics.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByClassGradeId(Long classGradeId);
    List<Section> findByClassTeacherId(Long classTeacherId);
}
