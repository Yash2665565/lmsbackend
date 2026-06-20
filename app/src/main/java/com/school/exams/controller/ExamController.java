package com.school.exams.controller;

import com.school.common.dto.ApiResponse;
import com.school.exams.dto.*;
import com.school.exams.service.ExamService;
import com.school.identity.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    // GET /api/exams?termId=
    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ExamDto>>> listExams(
            @RequestParam Long termId) {
        List<ExamDto> exams = examService.listByTerm(termId);
        return ResponseEntity.ok(ApiResponse.ok(exams));
    }

    // POST /api/exams
    @PostMapping("/exams")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExamDto>> createExam(
            @RequestBody @Valid ExamDto req) {
        ExamDto created = examService.createExam(req);
        return ResponseEntity.ok(ApiResponse.ok("Exam created successfully", created));
    }

    // GET /api/exams/{id}/subjects
    @GetMapping("/exams/{id}/subjects")
    public ResponseEntity<ApiResponse<List<ExamSubjectDto>>> listExamSubjects(
            @PathVariable Long id) {
        List<ExamSubjectDto> subjects = examService.listExamSubjects(id);
        return ResponseEntity.ok(ApiResponse.ok(subjects));
    }

    // POST /api/exams/{id}/subjects
    @PostMapping("/exams/{id}/subjects")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExamSubjectDto>> addExamSubject(
            @PathVariable Long id,
            @RequestBody @Valid ExamSubjectDto req) {
        req.setExamId(id);
        ExamSubjectDto created = examService.addExamSubject(req);
        return ResponseEntity.ok(ApiResponse.ok("Exam subject added successfully", created));
    }

    // GET /api/exam-subjects/{examSubjectId}/marks
    @GetMapping("/exam-subjects/{examSubjectId}/marks")
    public ResponseEntity<ApiResponse<List<MarkDto>>> getMarks(
            @PathVariable Long examSubjectId) {
        List<MarkDto> marks = examService.getMarks(examSubjectId);
        return ResponseEntity.ok(ApiResponse.ok(marks));
    }

    // POST /api/exam-subjects/{examSubjectId}/marks
    @PostMapping("/exam-subjects/{examSubjectId}/marks")
    @PreAuthorize("hasAnyRole('SUBJECT_TEACHER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MarkDto>> enterMark(
            @PathVariable Long examSubjectId,
            @RequestBody @Valid MarkEntryRequest req,
            @AuthenticationPrincipal User currentUser) {
        MarkDto mark = examService.enterMark(examSubjectId, req, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Mark entered successfully", mark));
    }

    // GET /api/students/{studentId}/report-card?termId=
    @GetMapping("/students/{studentId}/report-card")
    public ResponseEntity<ApiResponse<ReportCardDto>> getReportCard(
            @PathVariable Long studentId,
            @RequestParam Long termId) {
        ReportCardDto reportCard = examService.getReportCard(studentId, termId);
        return ResponseEntity.ok(ApiResponse.ok(reportCard));
    }
}
