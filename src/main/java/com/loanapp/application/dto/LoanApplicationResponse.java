package com.loanapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanApplicationResponse {

    private Long id;
    private BigDecimal requestedAmount;
    private Integer requestedTenure;
    private String purpose;
    private Integer eligibilityScore;
    private String status;
    private LocalDateTime appliedAt;

    private String productName;

    //admin
    private String userEmail;
    private String adminRemarks;
}