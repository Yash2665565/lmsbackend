package com.school.lms.controller;

import com.school.common.dto.ApiResponse;
import com.school.identity.entity.User;
import com.school.lms.dto.*;
import com.school.lms.entity.CourseNote;
import com.school.lms.service.LmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LmsController {

    private final LmsService lmsService;

    // ── Courses (scoped subject listing) ─────────────────────────────────────

    @GetMapping("/lms/courses")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> myCourses(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long sectionId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.listCourses(user, sectionId)));
    }

    @GetMapping("/lms/my-sections")
    @PreAuthorize("hasAnyRole('TEACHER','CLASS_TEACHER','SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> myTeachingSections(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.myTeachingSections(user)));
    }

    // ── Units ──────────────────────────────────────────────────────────────

    @GetMapping("/subjects/{subjectId}/units")
    public ResponseEntity<ApiResponse<List<UnitDto>>> listUnits(@PathVariable Long subjectId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.listUnits(subjectId)));
    }

    @PostMapping("/subjects/{subjectId}/units")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<UnitDto>> createUnit(
            @PathVariable Long subjectId,
            @RequestBody UnitRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.createUnit(subjectId, req.title(), req.description(), req.orderNo())));
    }

    @PutMapping("/units/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<UnitDto>> updateUnit(
            @PathVariable Long unitId,
            @RequestBody UnitRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.updateUnit(unitId, req.title(), req.description(), req.orderNo())));
    }

    @DeleteMapping("/units/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUnit(@PathVariable Long unitId) {
        lmsService.deleteUnit(unitId);
        return ResponseEntity.ok(ApiResponse.ok("Unit deleted", null));
    }

    // ── Notes ──────────────────────────────────────────────────────────────

    @GetMapping("/units/{unitId}/notes")
    public ResponseEntity<ApiResponse<List<CourseNoteDto>>> listNotes(@PathVariable Long unitId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.listNotes(unitId)));
    }

    @PostMapping("/units/{unitId}/notes")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<CourseNoteDto>> createNote(
            @PathVariable Long unitId,
            @RequestBody NoteRequest req) {
        CourseNote.NoteType type = req.noteType() != null
                ? CourseNote.NoteType.valueOf(req.noteType())
                : CourseNote.NoteType.TEXT;
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.createNote(unitId, req.title(), req.content(), req.fileUrl(), type)));
    }

    @DeleteMapping("/notes/{noteId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long noteId) {
        lmsService.deleteNote(noteId);
        return ResponseEntity.ok(ApiResponse.ok("Note deleted", null));
    }

    // ── Assignments ────────────────────────────────────────────────────────

    @GetMapping("/units/{unitId}/assignments")
    public ResponseEntity<ApiResponse<List<AssignmentDto>>> listAssignments(
            @PathVariable Long unitId,
            @RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.listAssignments(unitId, studentId)));
    }

    @PostMapping("/units/{unitId}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentDto>> createAssignment(
            @PathVariable Long unitId,
            @RequestBody AssignmentRequest req) {
        LocalDate due = req.dueDate() != null ? LocalDate.parse(req.dueDate()) : null;
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.createAssignment(unitId, req.title(), req.description(),
                        due, req.maxMarks() > 0 ? req.maxMarks() : 100, null)));
    }

    @DeleteMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long assignmentId) {
        lmsService.deleteAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.ok("Assignment deleted", null));
    }

    // ── Submissions ────────────────────────────────────────────────────────

    @PostMapping("/assignments/{assignmentId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> submit(
            @PathVariable Long assignmentId,
            @RequestBody SubmitRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.submit(assignmentId, req.studentId(), req.content())));
    }

    @GetMapping("/assignments/{assignmentId}/my-submission")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> mySubmission(
            @PathVariable Long assignmentId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.getMySubmission(assignmentId, studentId)));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionDto>>> listSubmissions(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.listSubmissions(assignmentId)));
    }

    @PatchMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> grade(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.gradeSubmission(submissionId, req.marks(), req.feedback())));
    }

    // ── Surprise Tests ─────────────────────────────────────────────────────

    @GetMapping("/units/{unitId}/surprise-tests")
    public ResponseEntity<ApiResponse<List<SurpriseTestDto>>> listSurpriseTests(@PathVariable Long unitId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.listSurpriseTests(unitId)));
    }

    @PostMapping("/units/{unitId}/surprise-tests")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<SurpriseTestDto>> createSurpriseTest(
            @PathVariable Long unitId,
            @RequestBody SurpriseTestRequest req) {
        LocalDate date = req.testDate() != null ? LocalDate.parse(req.testDate()) : null;
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.createSurpriseTest(unitId, req.title(), req.description(),
                        date, req.durationMinutes(), req.maxMarks(), null)));
    }

    @PutMapping("/surprise-tests/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','TEACHER','CLASS_TEACHER','SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<SurpriseTestDto>> updateSurpriseTest(
            @PathVariable Long testId,
            @RequestBody SurpriseTestRequest req) {
        LocalDate date = req.testDate() != null ? LocalDate.parse(req.testDate()) : null;
        return ResponseEntity.ok(ApiResponse.ok(
                lmsService.updateSurpriseTest(testId, req.title(), req.description(),
                        date, req.durationMinutes(), req.maxMarks())));
    }

    @DeleteMapping("/surprise-tests/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSurpriseTest(@PathVariable Long testId) {
        lmsService.deleteSurpriseTest(testId);
        return ResponseEntity.ok(ApiResponse.ok("Surprise test deleted", null));
    }

    // ── Stats ──────────────────────────────────────────────────────────────

    @GetMapping("/students/{studentId}/lms-stats")
    public ResponseEntity<ApiResponse<LmsStatsDto>> studentStats(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(lmsService.getStudentStats(studentId)));
    }

    // ── Request records ────────────────────────────────────────────────────

    record UnitRequest(String title, String description, int orderNo) {}
    record NoteRequest(String title, String content, String fileUrl, String noteType) {}
    record AssignmentRequest(String title, String description, String dueDate, int maxMarks) {}
    record SubmitRequest(Long studentId, String content) {}
    record GradeRequest(BigDecimal marks, String feedback) {}
    record SurpriseTestRequest(String title, String description, String testDate, int durationMinutes, int maxMarks) {}
}
