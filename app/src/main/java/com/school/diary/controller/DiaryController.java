package com.school.diary.controller;

import com.school.common.dto.ApiResponse;
import com.school.diary.dto.DiaryCreateRequest;
import com.school.diary.dto.DiaryDto;
import com.school.diary.service.DiaryService;
import com.school.identity.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService service;

    // Diary entries for a section (teachers/admin viewing a section)
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<List<DiaryDto>>> bySection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listForSection(sectionId)));
    }

    // A student's diary (their section's entries, with the student's own response)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<DiaryDto>>> forStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(service.studentDiary(studentId)));
    }

    // Student marks a diary entry done / not-done and/or leaves a note
    @PostMapping("/{diaryId}/respond")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> respond(@PathVariable Long diaryId,
                                                                     @RequestBody RespondRequest req,
                                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok("Saved", service.respond(user, diaryId, req.status(), req.note())));
    }

    // Teacher/admin: response report for a section (optionally filtered by date)
    @GetMapping("/section/{sectionId}/responses")
    @PreAuthorize("hasAnyRole('TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> responses(@PathVariable Long sectionId,
                                                                            @RequestParam(required = false) String date) {
        java.time.LocalDate d = (date == null || date.isBlank()) ? null : java.time.LocalDate.parse(date);
        return ResponseEntity.ok(ApiResponse.ok(service.sectionResponses(sectionId, d)));
    }

    public record RespondRequest(String status, String note) {}

    // Sections the logged-in teacher is class-teacher of
    @GetMapping("/my-sections")
    @PreAuthorize("hasAnyRole('TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> mySections(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(service.mySections(user)));
    }

    // (section + subject) allocations the logged-in teacher teaches
    @GetMapping("/my-allocations")
    @PreAuthorize("hasAnyRole('TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> myAllocations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(service.myAllocations(user)));
    }

    // Post a diary entry (class teacher of the section, or admin)
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DiaryDto>> create(@RequestBody DiaryCreateRequest req,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok("Diary posted", service.create(req, user)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }

    // ── Admin: assign class teachers to sections ──
    @GetMapping("/sections")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> adminSections() {
        return ResponseEntity.ok(ApiResponse.ok(service.adminSections()));
    }

    @PutMapping("/sections/{sectionId}/class-teacher")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assign(@PathVariable Long sectionId, @RequestBody AssignRequest req) {
        service.assignClassTeacher(sectionId, req.teacherId());
        return ResponseEntity.ok(ApiResponse.ok("Class teacher assigned", null));
    }

    public record AssignRequest(Long teacherId) {}
}
