package com.school.transport.controller;

import com.school.common.dto.ApiResponse;
import com.school.transport.dto.BusDto;
import com.school.transport.dto.RouteDto;
import com.school.transport.dto.StudentTransportDto;
import com.school.transport.entity.Bus;
import com.school.transport.service.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService service;

    /* ── Routes ── */
    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<RouteDto>>> listRoutes() {
        return ResponseEntity.ok(ApiResponse.ok(service.listRoutes()));
    }

    @PostMapping("/routes")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RouteDto>> createRoute(@RequestBody RouteRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.createRoute(req.name(), req.description(), req.fare())));
    }

    @PutMapping("/routes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RouteDto>> updateRoute(@PathVariable Long id, @RequestBody RouteRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateRoute(id, req.name(), req.description(), req.fare())));
    }

    @DeleteMapping("/routes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        service.deleteRoute(id);
        return ResponseEntity.ok(ApiResponse.ok("Route deleted", null));
    }

    /* ── Buses ── */
    @GetMapping("/buses")
    public ResponseEntity<ApiResponse<List<BusDto>>> listBuses() {
        return ResponseEntity.ok(ApiResponse.ok(service.listBuses()));
    }

    @PostMapping("/buses")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BusDto>> createBus(@RequestBody BusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.createBus(req.toEntity())));
    }

    @PutMapping("/buses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BusDto>> updateBus(@PathVariable Long id, @RequestBody BusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateBus(id, req.toEntity())));
    }

    @DeleteMapping("/buses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBus(@PathVariable Long id) {
        service.deleteBus(id);
        return ResponseEntity.ok(ApiResponse.ok("Bus deleted", null));
    }

    /* ── Assignments ── */
    @GetMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentTransportDto>>> listAssignments() {
        return ResponseEntity.ok(ApiResponse.ok(service.listAssignments()));
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentTransportDto>> assign(@RequestBody AssignRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.assign(req.studentId(), req.busId(), req.pickupStop())));
    }

    @DeleteMapping("/assignments/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unassign(@PathVariable Long studentId) {
        service.unassign(studentId);
        return ResponseEntity.ok(ApiResponse.ok("Assignment removed", null));
    }

    /* ── Student's own transport ── */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<StudentTransportDto>> forStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getForStudent(studentId)));
    }

    /* ── Request bodies ── */
    public record RouteRequest(String name, String description, BigDecimal fare) {}

    public record AssignRequest(Long studentId, Long busId, String pickupStop) {}

    public record BusRequest(String busNumber, Long routeId, Integer capacity,
                             String driverName, String driverPhone,
                             String conductorName, String conductorPhone,
                             String pickupTime, String dropTime) {
        Bus toEntity() {
            Bus b = new Bus();
            b.setBusNumber(busNumber);
            b.setRouteId(routeId);
            b.setCapacity(capacity);
            b.setDriverName(driverName);
            b.setDriverPhone(driverPhone);
            b.setConductorName(conductorName);
            b.setConductorPhone(conductorPhone);
            b.setPickupTime(pickupTime);
            b.setDropTime(dropTime);
            return b;
        }
    }
}
