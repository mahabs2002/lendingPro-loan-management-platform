package com.loanapp.auth.dto;

import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String fullName;
}
