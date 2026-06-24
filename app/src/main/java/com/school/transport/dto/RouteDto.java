package com.school.transport.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RouteDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal fare;
    private long busCount;
}
