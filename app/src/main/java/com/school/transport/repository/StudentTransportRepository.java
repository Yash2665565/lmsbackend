package com.school.transport.repository;

import com.school.transport.entity.StudentTransport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentTransportRepository extends JpaRepository<StudentTransport, Long> {
    Optional<StudentTransport> findByStudentId(Long studentId);
    List<StudentTransport> findByBusId(Long busId);
    long countByBusId(Long busId);
}
