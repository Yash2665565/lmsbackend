package com.school.timetable.controller;

import com.school.common.dto.ApiResponse;
import com.school.timetable.dto.PeriodDto;
import com.school.timetable.dto.TimetableSlotDto;
import com.school.timetable.dto.TimetableSlotRequest;
import com.school.timetable.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @GetMapping("/periods")
    public ResponseEntity<ApiResponse<List<PeriodDto>>> listPeriods() {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.listPeriods()));
    }

    @PostMapping("/periods")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PeriodDto>> createPeriod(@Valid @RequestBody PeriodDto req) {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.createPeriod(req)));
    }

    @GetMapping("/sections/{sectionId}/timetable")
    public ResponseEntity<ApiResponse<List<TimetableSlotDto>>> getSectionTimetable(
            @PathVariable Long sectionId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.getSectionTimetable(sectionId, academicYearId)));
    }

    @GetMapping("/students/{studentId}/timetable")
    public ResponseEntity<ApiResponse<List<TimetableSlotDto>>> getStudentTimetable(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.getStudentTimetable(studentId)));
    }

    @PostMapping("/timetable-slots")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TimetableSlotDto>> createSlot(
            @Valid @RequestBody TimetableSlotRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.createSlot(req)));
    }

    @DeleteMapping("/timetable-slots/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable Long id) {
        timetableService.deleteSlot(id);
        return ResponseEntity.ok(ApiResponse.ok("Timetable slot deleted", null));
    }
}
