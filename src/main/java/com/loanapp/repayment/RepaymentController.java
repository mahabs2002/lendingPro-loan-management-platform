package com.loanapp.repayment;

import com.loanapp.auth.UserRepository;
import com.loanapp.common.ApiResponse;
import com.loanapp.entity.Payment;
import com.loanapp.repayment.dto.PaymentRequest;
import com.loanapp.repayment.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
public class RepaymentController {

    private final RepaymentService repaymentService;
    private final UserRepository userRepository;

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody PaymentRequest req) {
        Long userId = userRepository.findByEmail(ud.getUsername()).orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok("Payment successful", repaymentService.makePayment(userId, req)));
    }

    @GetMapping("/history/{loanAccountId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> history(@PathVariable Long loanAccountId) {
        return ResponseEntity.ok(ApiResponse.ok(repaymentService.getPaymentHistory(loanAccountId)));
    }
}
