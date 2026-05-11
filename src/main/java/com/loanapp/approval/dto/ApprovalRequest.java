package com.loanapp.approval.dto;

import com.loanapp.enums.LoanApplicationStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ApprovalRequest {
    @NotNull private LoanApplicationStatus decision;
    private BigDecimal approvedAmount;
    private Integer approvedTenure;
    private String remarks;
}
