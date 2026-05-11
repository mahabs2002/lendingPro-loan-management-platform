package com.loanapp.kyc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KycResponse {

    private Long id;
    private String documentType;
    private String documentNumber;

    private String fileName;
    private String fileType;
    private Long fileSize;

    private String status;
    private String remarks;

    private LocalDateTime createdAt;

    // Admin info
    private Long verifiedBy;
}
