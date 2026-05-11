
package com.loanapp.kyc.dto;

import com.loanapp.enums.DocumentType;
import jakarta.validation.constraints.*;
        import lombok.Data;

@Data
public class KycUploadDto {
    @NotNull  private DocumentType documentType;
    @NotBlank private String documentNumber;

}
