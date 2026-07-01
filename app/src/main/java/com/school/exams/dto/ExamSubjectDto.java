package com.school.exams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubjectDto {

    private Long id;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private Long classGradeId;
    private String className;
    private int maxMarks;
    private LocalDate examDate;
    private String startTime;
}
