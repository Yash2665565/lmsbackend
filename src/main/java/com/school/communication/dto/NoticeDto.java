package com.school.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    private Long id;
    private String name;
    private String content;
    private String mandatory;
    private String targetType;
    private Long targetId;
    private LocalDateTime createdAt;
}
