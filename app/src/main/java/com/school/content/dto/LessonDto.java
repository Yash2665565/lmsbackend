package com.school.content.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LessonDto {
    private Long id;
    private String name;
    private String title;
    private String about;
    private String video;
    private String moduleType;
    private String description;
    private LocalDateTime createdAt;
}
