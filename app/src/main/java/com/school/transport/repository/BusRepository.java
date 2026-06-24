package com.school.transport.repository;

import com.school.transport.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByRouteId(Long routeId);
    long countByRouteId(Long routeId);
}
