package com.school.fees.repository;

import com.school.fees.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findBySectionId(Long sectionId);
    long countByFeeHeadId(Long feeHeadId);
}
