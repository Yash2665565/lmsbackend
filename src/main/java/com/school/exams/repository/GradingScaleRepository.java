package com.school.exams.repository;

import com.school.exams.entity.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    @Query("SELECT g FROM GradingScale g WHERE :pct BETWEEN g.minPct AND g.maxPct")
    Optional<GradingScale> findByPct(BigDecimal pct);
}
