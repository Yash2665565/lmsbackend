package com.school.transport.service;

import com.school.transport.dto.BusDto;
import com.school.transport.dto.RouteDto;
import com.school.transport.dto.StudentTransportDto;
import com.school.transport.entity.Bus;
import com.school.transport.entity.StudentTransport;
import com.school.transport.entity.TransportRoute;
import com.school.transport.repository.BusRepository;
import com.school.transport.repository.StudentTransportRepository;
import com.school.transport.repository.TransportRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportRouteRepository routeRepo;
    private final BusRepository busRepo;
    private final StudentTransportRepository assignRepo;

    /* ── Routes ─────────────────────────────────────────────── */

    public List<RouteDto> listRoutes() {
        return routeRepo.findAll().stream().map(this::toRouteDto).collect(Collectors.toList());
    }

    @Transactional
    public RouteDto createRoute(String name, String description, java.math.BigDecimal fare) {
        TransportRoute r = new TransportRoute();
        r.setName(name);
        r.setDescription(description);
        r.setFare(fare);
        return toRouteDto(routeRepo.save(r));
    }

    @Transactional
    public RouteDto updateRoute(Long id, String name, String description, java.math.BigDecimal fare) {
        TransportRoute r = routeRepo.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
        if (name != null) r.setName(name);
        r.setDescription(description);
        r.setFare(fare);
        return toRouteDto(routeRepo.save(r));
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeRepo.deleteById(id);
    }

    /* ── Buses ──────────────────────────────────────────────── */

    public List<BusDto> listBuses() {
        Map<Long, String> routeNames = routeRepo.findAll().stream()
                .collect(Collectors.toMap(TransportRoute::getId, TransportRoute::getName));
        return busRepo.findAll().stream().map(b -> toBusDto(b, routeNames)).collect(Collectors.toList());
    }

    @Transactional
    public BusDto createBus(Bus body) {
        Bus saved = busRepo.save(body);
        return toBusDto(saved, routeNameMap());
    }

    @Transactional
    public BusDto updateBus(Long id, Bus body) {
        Bus b = busRepo.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
        b.setBusNumber(body.getBusNumber());
        b.setRouteId(body.getRouteId());
        b.setCapacity(body.getCapacity());
        b.setDriverName(body.getDriverName());
        b.setDriverPhone(body.getDriverPhone());
        b.setConductorName(body.getConductorName());
        b.setConductorPhone(body.getConductorPhone());
        b.setPickupTime(body.getPickupTime());
        b.setDropTime(body.getDropTime());
        return toBusDto(busRepo.save(b), routeNameMap());
    }

    @Transactional
    public void deleteBus(Long id) {
        busRepo.deleteById(id);
    }

    /* ── Assignments ────────────────────────────────────────── */

    public List<StudentTransportDto> listAssignments() {
        return assignRepo.findAll().stream().map(this::toAssignmentDto).collect(Collectors.toList());
    }

    public StudentTransportDto getForStudent(Long studentId) {
        return assignRepo.findByStudentId(studentId).map(this::toAssignmentDto).orElse(null);
    }

    @Transactional
    public StudentTransportDto assign(Long studentId, Long busId, String pickupStop) {
        StudentTransport st = assignRepo.findByStudentId(studentId).orElseGet(StudentTransport::new);
        st.setStudentId(studentId);
        st.setBusId(busId);
        st.setPickupStop(pickupStop);
        return toAssignmentDto(assignRepo.save(st));
    }

    @Transactional
    public void unassign(Long studentId) {
        assignRepo.findByStudentId(studentId).ifPresent(assignRepo::delete);
    }

    /* ── Mappers ────────────────────────────────────────────── */

    private Map<Long, String> routeNameMap() {
        return routeRepo.findAll().stream()
                .collect(Collectors.toMap(TransportRoute::getId, TransportRoute::getName));
    }

    private RouteDto toRouteDto(TransportRoute r) {
        return RouteDto.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .fare(r.getFare())
                .busCount(busRepo.countByRouteId(r.getId()))
                .build();
    }

    private BusDto toBusDto(Bus b, Map<Long, String> routeNames) {
        return BusDto.builder()
                .id(b.getId())
                .busNumber(b.getBusNumber())
                .routeId(b.getRouteId())
                .routeName(b.getRouteId() == null ? null : routeNames.get(b.getRouteId()))
                .capacity(b.getCapacity())
                .driverName(b.getDriverName())
                .driverPhone(b.getDriverPhone())
                .conductorName(b.getConductorName())
                .conductorPhone(b.getConductorPhone())
                .pickupTime(b.getPickupTime())
                .dropTime(b.getDropTime())
                .assignedCount(assignRepo.countByBusId(b.getId()))
                .build();
    }

    private StudentTransportDto toAssignmentDto(StudentTransport st) {
        Bus b = st.getBusId() == null ? null : busRepo.findById(st.getBusId()).orElse(null);
        String routeName = null;
        if (b != null && b.getRouteId() != null) {
            routeName = routeRepo.findById(b.getRouteId()).map(TransportRoute::getName).orElse(null);
        }
        StudentTransportDto.StudentTransportDtoBuilder dto = StudentTransportDto.builder()
                .id(st.getId())
                .studentId(st.getStudentId())
                .busId(st.getBusId())
                .pickupStop(st.getPickupStop());
        if (b != null) {
            dto.busNumber(b.getBusNumber())
               .routeName(routeName)
               .pickupTime(b.getPickupTime())
               .dropTime(b.getDropTime())
               .driverName(b.getDriverName())
               .driverPhone(b.getDriverPhone())
               .conductorName(b.getConductorName())
               .conductorPhone(b.getConductorPhone());
        }
        return dto.build();
    }
}
