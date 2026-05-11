package com.loanapp.repayment.dto;

import com.loanapp.enums.PaymentMode;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull private Long loanAccountId;
    private Long emiId;
    @NotNull @DecimalMin("1") private BigDecimal amountPaid;
    @NotNull private PaymentMode paymentMode;
}
