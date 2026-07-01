package com.school.academics.controller;

import com.school.academics.repository.SubjectTeacherRepository.SubjectRow;
import com.school.academics.service.AllocationService;
import com.school.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService service;

    // Subjects of a section + assigned teacher each
    @GetMapping("/sections/{sectionId}/subject-teachers")
    public ResponseEntity<ApiResponse<List<SubjectRow>>> sectionSubjects(@PathVariable Long sectionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.sectionSubjects(sectionId)));
    }

    // Assign (or reassign) a teacher to a subject in a section
    @PostMapping("/subject-teachers")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assign(@RequestBody AssignRequest req) {
        service.assign(req.sectionId(), req.topicId(), req.teacherId());
        return ResponseEntity.ok(ApiResponse.ok("Teacher assigned", null));
    }

    @DeleteMapping("/subject-teachers/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unassign(@PathVariable Long assignmentId) {
        service.unassign(assignmentId);
        return ResponseEntity.ok(ApiResponse.ok("Removed", null));
    }

    // Full allocation report for a teacher
    @GetMapping("/teachers/{teacherId}/allocation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> teacherReport(@PathVariable Long teacherId) {
        return ResponseEntity.ok(ApiResponse.ok(service.teacherReport(teacherId)));
    }

    // ── Teacher subject expertise ──
    @GetMapping("/teachers/{teacherId}/subjects")
    public ResponseEntity<?> getExpertise(@PathVariable Long teacherId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getExpertise(teacherId)));
    }

    @PutMapping("/teachers/{teacherId}/subjects")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> setExpertise(@PathVariable Long teacherId, @RequestBody ExpertiseRequest req) {
        service.setExpertise(teacherId, req.subjectIds());
        return ResponseEntity.ok(ApiResponse.ok("Expertise saved", null));
    }

    // Teachers qualified to teach a subject (for the allocation dropdown)
    @GetMapping("/subjects/{topicId}/teachers")
    public ResponseEntity<?> teachersForSubject(@PathVariable Long topicId) {
        return ResponseEntity.ok(ApiResponse.ok(service.teachersForSubject(topicId)));
    }

    // ── Add / remove a subject to a class (class_subjects) ──
    @PostMapping("/classes/{classGradeId}/subjects")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addClassSubject(@PathVariable Long classGradeId, @RequestBody ClassSubjectRequest req) {
        service.addClassSubject(classGradeId, req.subjectId());
        return ResponseEntity.ok(ApiResponse.ok("Subject added to class", null));
    }

    @DeleteMapping("/classes/{classGradeId}/subjects/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeClassSubject(@PathVariable Long classGradeId, @PathVariable Long subjectId) {
        service.removeClassSubject(classGradeId, subjectId);
        return ResponseEntity.ok(ApiResponse.ok("Subject removed from class", null));
    }

    public record AssignRequest(Long sectionId, Long topicId, Long teacherId) {}
    public record ExpertiseRequest(List<Long> subjectIds) {}
    public record ClassSubjectRequest(Long subjectId) {}
}
