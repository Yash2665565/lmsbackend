package com.school.exams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
