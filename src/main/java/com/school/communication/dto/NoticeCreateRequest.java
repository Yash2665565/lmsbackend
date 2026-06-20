package com.school.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String content;

    private String targetType;
    private Long targetId;
    private String mandatory;
}
