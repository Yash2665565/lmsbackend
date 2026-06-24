package com.school.fees.controller;

import com.school.common.dto.ApiResponse;
import com.school.fees.dto.CollectionRowDto;
import com.school.fees.dto.FeeHeadDto;
import com.school.fees.dto.FeeStructureDto;
import com.school.fees.dto.StudentFeeDto;
import com.school.fees.service.FeesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeesController {

    private final FeesService service;

    /* ── Fee heads ── */
    @GetMapping("/heads")
    public ResponseEntity<ApiResponse<List<FeeHeadDto>>> heads() {
        return ResponseEntity.ok(ApiResponse.ok(service.listHeads()));
    }

    @PostMapping("/heads")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeeHeadDto>> createHead(@RequestBody HeadRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.createHead(req.name(), req.description())));
    }

    @PutMapping("/heads/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeeHeadDto>> updateHead(@PathVariable Long id, @RequestBody HeadRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateHead(id, req.name(), req.description())));
    }

    @DeleteMapping("/heads/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteHead(@PathVariable Long id) {
        service.deleteHead(id);
        return ResponseEntity.ok(ApiResponse.ok("Fee head deleted", null));
    }

    /* ── Structure per section ── */
    @GetMapping("/structures")
    public ResponseEntity<ApiResponse<List<FeeStructureDto>>> structures(@RequestParam Long sectionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listStructures(sectionId)));
    }

    @PostMapping("/structures")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeeStructureDto>> upsertStructure(@RequestBody StructureRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                service.upsertStructure(req.sectionId(), req.feeHeadId(), req.amount(), req.frequency())));
    }

    @DeleteMapping("/structures/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStructure(@PathVariable Long id) {
        service.deleteStructure(id);
        return ResponseEntity.ok(ApiResponse.ok("Removed", null));
    }

    /* ── Student fee sheet ── */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<StudentFeeDto>> studentFees(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getStudentFees(studentId)));
    }

    /* ── Admin collections ── */
    @GetMapping("/collections")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CollectionRowDto>>> collections(@RequestParam Long sectionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getSectionCollections(sectionId)));
    }

    @PostMapping("/student-status")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@RequestBody StatusRequest req) {
        service.updateStudentFeeStatus(req.studentId(), req.feeStructureId(), req.status(), req.amountPaid(), req.remarks());
        return ResponseEntity.ok(ApiResponse.ok("Updated", null));
    }

    /* ── Request bodies ── */
    public record HeadRequest(String name, String description) {}
    public record StructureRequest(Long sectionId, Long feeHeadId, BigDecimal amount, String frequency) {}
    public record StatusRequest(Long studentId, Long feeStructureId, String status, BigDecimal amountPaid, String remarks) {}
}
