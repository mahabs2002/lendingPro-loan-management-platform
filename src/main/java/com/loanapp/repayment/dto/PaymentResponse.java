package com.loanapp.repayment.dto;

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
public class PaymentResponse {

    private Long id;
    private String paymentReference;
    private BigDecimal amountPaid;
    private String paymentMode;
    private String paymentStatus;
    private LocalDateTime paidAt;

    private String loanAccountNumber;
    private Integer installmentNo; // from EMI (optional)
}
