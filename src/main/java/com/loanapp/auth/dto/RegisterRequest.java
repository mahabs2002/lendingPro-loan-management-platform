package com.loanapp.auth.dto;

import com.loanapp.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String fullName;
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 10, max = 10) private String mobile;
    @NotBlank @Size(min = 8) private String password;
    private Role role = Role.USER;
}
