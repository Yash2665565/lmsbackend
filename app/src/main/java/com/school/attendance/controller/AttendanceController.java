package com.school.attendance.controller;

import com.school.attendance.dto.AttendanceRecordDto;
import com.school.attendance.dto.AttendanceSubmitRequest;
import com.school.attendance.dto.AttendanceSummaryDto;
import com.school.attendance.service.AttendanceService;
import com.school.common.dto.ApiResponse;
import com.school.identity.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Submit (or update) attendance for all students in a section.
     * Roles allowed: CLASS_TEACHER, SUBJECT_TEACHER, ADMIN, SUPER_ADMIN
     */
    @PostMapping("/sections/{sectionId}/attendance")
    @PreAuthorize("hasAnyRole('CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> submitAttendance(
            @PathVariable Long sectionId,
            @Valid @RequestBody AttendanceSubmitRequest req) {

        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        attendanceService.submitAttendance(sectionId, req, teacher);
        return ResponseEntity.ok(ApiResponse.ok("Attendance submitted successfully", null));
    }

    /**
     * Fetch the daily attendance register for a section on a given date.
     */
    @GetMapping("/sections/{sectionId}/attendance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getDailyRegister(
            @PathVariable Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<AttendanceRecordDto> records = attendanceService.getDailyRegister(sectionId, date);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    /**
     * Fetch term-wise attendance summaries for a student.
     */
    @GetMapping("/students/{studentId}/attendance-summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getStudentSummary(
            @PathVariable Long studentId) {

        List<AttendanceSummaryDto> summaries = attendanceService.getStudentSummary(studentId);
        return ResponseEntity.ok(ApiResponse.ok(summaries));
    }

    /**
     * Report: students whose attendance percentage is below the given threshold for a term.
     * Default threshold = 75%.
     */
    @GetMapping("/reports/attendance/low")
    @PreAuthorize("hasAnyRole('CLASS_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getLowAttendance(
            @RequestParam Long termId,
            @RequestParam(defaultValue = "75") BigDecimal threshold) {

        List<AttendanceSummaryDto> result = attendanceService.getLowAttendance(termId, threshold);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Report: students marked ABSENT on a specific date (daily attendance only).
     */
    @GetMapping("/reports/attendance/daily")
    @PreAuthorize("hasAnyRole('CLASS_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getDailyAbsentees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<AttendanceSummaryDto> result = attendanceService.getDailyAbsentees(date);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
