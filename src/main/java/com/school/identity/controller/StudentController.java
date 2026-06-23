package com.school.identity.controller;

import com.school.common.dto.ApiResponse;
import com.school.identity.dto.StudentCreateRequest;
import com.school.identity.dto.StudentDto;
import com.school.identity.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentDto>>> listStudents(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.listStudents(search, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentDto>> create(@RequestBody @Valid StudentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentDto>> update(
            @PathVariable Long id,
            @RequestBody @Valid StudentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.update(id, request)));
    }

    @PutMapping("/{id}/credentials")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentDto>> setCredentials(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("email and password are required"));
        }
        return ResponseEntity.ok(ApiResponse.ok(studentService.setCredentials(id, email, password)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
