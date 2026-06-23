package com.school.lms.dto;

import com.school.lms.entity.CourseNote.NoteType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class CourseNoteDto {
    private Long id;
    private Long unitId;
    private String title;
    private String content;
    private String fileUrl;
    private NoteType noteType;
    private LocalDateTime createdAt;
}
