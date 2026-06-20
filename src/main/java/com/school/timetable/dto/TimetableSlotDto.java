package com.school.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableSlotDto {
    private Long id;
    private Long sectionId;
    private String sectionName;
    private int dayOfWeek;
    private String dayName;
    private Long periodId;
    private String periodName;
    private String startTime;
    private String endTime;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
}
