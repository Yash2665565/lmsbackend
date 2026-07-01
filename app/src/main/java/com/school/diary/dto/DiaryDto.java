package com.school.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiaryDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long subjectId;
    private String subjectName;
    private Long sectionId;
    private String sectionName;
    private Long teacherId;
    private String teacherName;
    private LocalDateTime createdAt;
    // the requesting student's own response (null for teacher/admin views)
    private String myStatus;
    private String myNote;
}
