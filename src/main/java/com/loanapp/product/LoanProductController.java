package com.loanapp.product;

import com.loanapp.common.ApiResponse;
import com.loanapp.entity.LoanProduct;
import com.loanapp.product.dto.LoanProductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loan-products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanProduct>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(service.getActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanProduct>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping("/api/admin/loan-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanProduct>> create(@Valid @RequestBody LoanProductDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Product created", service.create(dto)));
    }

    @PutMapping("/api/admin/loan-products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanProduct>> update(@PathVariable Long id, @Valid @RequestBody LoanProductDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Product updated", service.update(id, dto)));
    }

    @PatchMapping("/api/admin/loan-products/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable Long id) {
        service.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled", null));
    }
}
