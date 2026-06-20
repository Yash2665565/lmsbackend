package com.school.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String employeeNo;
    private String qualification;
    private LocalDate joiningDate;
}
