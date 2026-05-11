package com.loanapp.emi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmiScheduleResponse {

    private Long id;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal emiAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal penaltyAmount;
    private String paidStatus;
    private LocalDate paidDate;

    private String loanAccountNumber; // useful for UI
}
