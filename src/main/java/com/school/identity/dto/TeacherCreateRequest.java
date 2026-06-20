package com.school.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCreateRequest {

    @NotBlank
    private String name;

    private String lname;

    @NotBlank
    @Email
    private String email;

    private String password;
    private String employeeNo;
    private String qualification;
    private LocalDate joiningDate;
}
