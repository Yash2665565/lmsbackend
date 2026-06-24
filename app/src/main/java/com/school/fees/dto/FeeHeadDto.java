package com.school.fees.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeeHeadDto {
    private Long id;
    private String name;
    private String description;
}
