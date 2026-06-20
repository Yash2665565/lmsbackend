package com.school.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDto {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private int sortOrder;
}
