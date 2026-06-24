package com.school.fees.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StudentFeeDto {
    private Long studentId;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;
    private List<Line> lines;

    @Data
    @Builder
    public static class Line {
        private Long feeStructureId;
        private String feeHeadName;
        private String frequency;
        private BigDecimal amount;
        private BigDecimal amountPaid;
        private BigDecimal due;
        private String status;   // PAID | PARTIAL | PENDING
        private String remarks;
    }
}
