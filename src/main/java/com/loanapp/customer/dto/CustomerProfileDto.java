package com.loanapp.customer.dto;

import com.loanapp.enums.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerProfileDto {
    @NotNull private LocalDate dob;
    @NotBlank private String gender;
    @NotNull private EmploymentType employmentType;
    private String employerName;
    @NotNull @DecimalMin("0.0") private BigDecimal monthlyIncome;
    @NotBlank private String addressLine1;
    @NotBlank private String city;
    @NotBlank private String state;
    @NotBlank @Size(min=6, max=6) private String pincode;
}
