package com.school.identity.controller;

import com.school.common.dto.ApiResponse;
import com.school.identity.dto.TeacherCreateRequest;
import com.school.identity.dto.TeacherDto;
import com.school.identity.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherDto>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TeacherDto>> create(@RequestBody @Valid TeacherCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TeacherDto>> update(
            @PathVariable Long id,
            @RequestBody TeacherCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(teacherService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
