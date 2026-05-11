package com.loanapp.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanProductDto {
    @NotBlank private String productName;
    @NotNull @DecimalMin("1000") private BigDecimal minAmount;
    @NotNull @DecimalMin("1000") private BigDecimal maxAmount;
    @NotNull @DecimalMin("1") @DecimalMax("50") private BigDecimal interestRate;
    @NotNull @Min(1) @Max(360) private Integer tenureMonths;
    @NotNull @DecimalMin("0") private BigDecimal processingFee;
    private Boolean activeStatus = true;
}
