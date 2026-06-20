package com.school.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSubmitRequest {

    @NotNull
    private LocalDate date;

    /** Null means daily attendance; non-null targets a specific period. */
    private Long periodId;

    @Valid
    @NotEmpty
    private List<AttendanceMarkDto> marks;
}
