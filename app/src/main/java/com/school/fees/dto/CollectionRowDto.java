package com.school.fees.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CollectionRowDto {
    private Long studentId;
    private String studentName;
    private String admissionNo;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;
    private String status;   // PAID | PARTIAL | PENDING
}
