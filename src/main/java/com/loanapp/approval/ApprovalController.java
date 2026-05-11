package com.loanapp.approval;

import com.loanapp.application.dto.LoanApplicationResponse;
import com.loanapp.approval.dto.ApprovalRequest;
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

@RestController
@RequestMapping("/api/admin/loans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final UserRepository userRepository;

    @PutMapping("/{applicationId}/decision")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> decide(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody ApprovalRequest req) {
        Long adminId = userRepository.findByEmail(ud.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok("Decision recorded",
                approvalService.processDecision(applicationId, adminId, req)));
    }

    @PatchMapping("/{applicationId}/review")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> markReview(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails ud) {
        Long adminId = userRepository.findByEmail(ud.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok("Marked under review",
                approvalService.markUnderReview(applicationId, adminId)));
    }
}
