package com.school.lms.repository;

import com.school.lms.entity.SurpriseTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurpriseTestRepository extends JpaRepository<SurpriseTest, Long> {
    List<SurpriseTest> findByUnitIdOrderByTestDateAsc(Long unitId);
}
