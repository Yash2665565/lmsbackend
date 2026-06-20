package com.school.communication.controller;

import com.school.common.dto.ApiResponse;
import com.school.communication.dto.NoticeCreateRequest;
import com.school.communication.dto.NoticeDto;
import com.school.communication.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeDto>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.listAll()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<NoticeDto>> create(@Valid @RequestBody NoticeCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.create(req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Notice deleted", null));
    }
}
