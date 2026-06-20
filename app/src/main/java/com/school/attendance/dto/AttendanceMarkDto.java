package com.school.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMarkDto {

    @NotNull
    private Long studentId;

    /**
     * One of: PRESENT, ABSENT, LATE, EXCUSED, HALF_DAY
     */
    @NotNull
    private String status;

    private String remarks;
}
