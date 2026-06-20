package com.school.academics.controller;

import com.school.academics.dto.*;
import com.school.academics.service.AcademicsService;
import com.school.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AcademicsController {

    private final AcademicsService academicsService;

    // ─── AcademicYear ────────────────────────────────────────────────────────

    @GetMapping("/api/academic-years")
    public ResponseEntity<ApiResponse<List<AcademicYearDto>>> listYears() {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.listYears()));
    }

    @PostMapping("/api/academic-years")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearDto>> createYear(@RequestBody AcademicYearDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.createYear(req)));
    }

    // ─── Term ────────────────────────────────────────────────────────────────

    @GetMapping("/api/terms")
    public ResponseEntity<ApiResponse<List<TermDto>>> listTerms(@RequestParam Long yearId) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.listTerms(yearId)));
    }

    @PostMapping("/api/terms")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TermDto>> createTerm(@RequestBody TermDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.createTerm(req)));
    }

    // ─── ClassGrade ──────────────────────────────────────────────────────────

    @GetMapping("/api/classes")
    public ResponseEntity<ApiResponse<List<ClassGradeDto>>> listClasses() {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.listClasses()));
    }

    @PostMapping("/api/classes")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClassGradeDto>> createClass(@RequestBody ClassGradeDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.createClass(req)));
    }

    // ─── Section ─────────────────────────────────────────────────────────────

    @GetMapping("/api/sections")
    public ResponseEntity<ApiResponse<List<SectionDto>>> listSections(@RequestParam Long classGradeId) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.listSections(classGradeId)));
    }

    @PostMapping("/api/sections")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SectionDto>> createSection(@RequestBody SectionDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.createSection(req)));
    }

    @PutMapping("/api/sections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SectionDto>> updateSection(
            @PathVariable Long id,
            @RequestBody SectionDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.updateSection(id, req)));
    }

    // ─── Subject ─────────────────────────────────────────────────────────────

    @GetMapping("/api/subjects")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> listSubjects() {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.listSubjects()));
    }

    @PostMapping("/api/subjects")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@RequestBody SubjectDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.createSubject(req)));
    }

    @PutMapping("/api/subjects/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectDto req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.updateSubject(id, req)));
    }

    // ─── Enrollment ──────────────────────────────────────────────────────────

    @GetMapping("/api/sections/{sectionId}/enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> listRoster(
            @PathVariable Long sectionId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.listRoster(sectionId, academicYearId)));
    }

    @PostMapping("/api/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentDto>> enroll(@Valid @RequestBody EnrollmentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(academicsService.enroll(req)));
    }

    @PatchMapping("/api/enrollments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        academicsService.updateEnrollmentStatus(id, body.get("status"));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
