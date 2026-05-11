package com.loanapp.account;

import com.loanapp.account.dto.LoanAccountResponse;
import com.loanapp.auth.UserRepository;
import com.loanapp.common.ApiResponse;
import com.loanapp.entity.LoanAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoanAccountController {

    private final LoanAccountService service;
    private final UserRepository userRepository;

    @GetMapping("/api/loans/my-accounts")
    public ResponseEntity<ApiResponse<List<LoanAccountResponse>>> myLoans(@AuthenticationPrincipal UserDetails ud) {
        Long userId = userRepository.findByEmail(ud.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok(service.getMyLoans(userId)));
    }

    @GetMapping("/api/loans/accounts/{id}")
    public ResponseEntity<ApiResponse<LoanAccountResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/api/admin/loans/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanAccountResponse>>> activeLoans() {
        return ResponseEntity.ok(ApiResponse.ok(service.getActiveLoans()));
    }
}
