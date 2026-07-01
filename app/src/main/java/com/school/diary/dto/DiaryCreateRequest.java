package com.school.diary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DiaryCreateRequest {
    @NotBlank
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long subjectId;
    private Long sectionId;
}
