package com.school.content.controller;

import com.school.common.dto.ApiResponse;
import com.school.content.dto.LessonDto;
import com.school.content.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/lessons")
    public ResponseEntity<ApiResponse<List<LessonDto>>> getAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(lessonService.getAll(search, page, size));
    }

    @GetMapping("/topics/{topicId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonDto>>> getByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(lessonService.getByTopic(topicId));
    }
}
