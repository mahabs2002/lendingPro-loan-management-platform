package com.loanapp.application;

import com.loanapp.application.dto.LoanApplicationRequest;
import com.loanapp.application.dto.LoanApplicationResponse;
import com.loanapp.auth.UserRepository;
import com.loanapp.common.ApiResponse;
import com.loanapp.entity.LoanApplication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService service;
    private final UserRepository userRepository;

    @PostMapping("/api/loans/apply")
    public ResponseEntity<ApiResponse<LoanApplication>> apply(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody LoanApplicationRequest req) {
        Long userId = getId(ud);
        return ResponseEntity.ok(ApiResponse.ok("Application submitted", service.apply(userId, req)));
    }

    @GetMapping("/api/loans/my-applications")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> myApplications(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok(service.getMyApplications(getId(ud))));
    }

    @GetMapping("/api/loans/my-applications/{id}")
    public ResponseEntity<ApiResponse<LoanApplication>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/api/admin/loans/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> pending() {
        return ResponseEntity.ok(ApiResponse.ok(service.getPendingApplications()));
    }

    @GetMapping("/api/admin/loans/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> all() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllApplications()));
    }

    private Long getId(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow().getId();
    }
}
