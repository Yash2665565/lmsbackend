package com.school.academics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
    private Long id;
    private Long classGradeId;
    private String classGradeName;
    private String name;
    private Long classTeacherId;
    private String classTeacherName;
}
