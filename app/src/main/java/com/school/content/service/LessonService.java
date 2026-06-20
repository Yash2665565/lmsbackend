package com.school.content.service;

import com.school.common.dto.ApiResponse;
import com.school.content.dto.LessonDto;
import com.school.content.entity.Lesson;
import com.school.content.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public ApiResponse<List<LessonDto>> getAll(String search, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Lesson> result = (search != null && !search.isBlank())
                ? lessonRepository.search(search, pr)
                : lessonRepository.findAll(pr);
        List<LessonDto> dtos = result.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.ok(dtos);
    }

    public ApiResponse<List<LessonDto>> getByTopic(Long topicId) {
        List<LessonDto> dtos = lessonRepository.findByTopicId(topicId)
                .stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.ok(dtos);
    }

    private LessonDto toDto(Lesson l) {
        LessonDto dto = new LessonDto();
        dto.setId(l.getId());
        dto.setName(l.getName());
        dto.setTitle(l.getTitle());
        dto.setAbout(l.getAbout());
        dto.setVideo(l.getVideo());
        dto.setModuleType(l.getModuleType());
        dto.setDescription(l.getDescription());
        dto.setCreatedAt(l.getCreatedAt());
        return dto;
    }
}
