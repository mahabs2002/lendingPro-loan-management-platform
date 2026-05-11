package com.loanapp.customer;

import com.loanapp.auth.UserRepository;
import com.loanapp.common.ApiResponse;
import com.loanapp.customer.dto.CustomerProfileDto;
import com.loanapp.entity.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/profile")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerProfile>> createProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CustomerProfileDto dto) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Profile saved", customerService.createOrUpdateProfile(userId, dto)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CustomerProfile>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CustomerProfileDto dto) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", customerService.createOrUpdateProfile(userId, dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CustomerProfile>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(customerService.getProfile(userId)));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }
}
