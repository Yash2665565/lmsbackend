package com.school.exams.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StudentExamDto {
    private Long examId;
    private String examName;
    private List<Paper> papers;

    @Data
    @Builder
    public static class Paper {
        private String subjectName;
        private LocalDate examDate;
        private String startTime;
        private int maxMarks;
    }
}
