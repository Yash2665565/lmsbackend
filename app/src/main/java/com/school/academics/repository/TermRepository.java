package com.school.academics.repository;

import com.school.academics.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findByAcademicYearId(Long academicYearId);

    @Query("SELECT t FROM Term t WHERE :date BETWEEN t.startDate AND t.endDate")
    Optional<Term> findTermForDate(LocalDate date);
}
