package com.loanapp.kyc.dto;

import com.loanapp.enums.KycStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class KycVerifyDto {
    @NotNull private KycStatus status;
    private String remarks;
}
