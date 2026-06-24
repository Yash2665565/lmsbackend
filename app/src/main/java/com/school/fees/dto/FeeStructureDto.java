package com.school.fees.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FeeStructureDto {
    private Long id;
    private Long sectionId;
    private Long feeHeadId;
    private String feeHeadName;
    private BigDecimal amount;
    private String frequency;
}
