package com.loanapp.account.dto;

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
public class LoanAccountResponse {

    private Long id;
    private String loanAccountNumber;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private LocalDate disbursedDate;
    private BigDecimal outstandingBalance;
    private String loanStatus;

    // from application
    private Long applicationId;
    private String productName;
}
