package com.loanapp.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {
    @NotNull private Long productId;
    @NotNull @DecimalMin("1000") private BigDecimal requestedAmount;
    @NotNull @Min(1) @Max(360) private Integer requestedTenure;
    @NotBlank private String purpose;
}
