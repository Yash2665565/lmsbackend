package com.school.timetable.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableSlotRequest {

    @NotNull
    private Long sectionId;

    @NotNull
    private Integer dayOfWeek;

    @NotNull
    private Long periodId;

    @NotNull
    private Long subjectId;

    @NotNull
    private Long teacherId;

    @NotNull
    private Long academicYearId;
}
