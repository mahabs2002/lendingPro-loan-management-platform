package com.loanapp.penalty;

import com.loanapp.common.ApiResponse;
import com.loanapp.entity.Penalty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/penalties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<Penalty>>> overdueReport() {
        return ResponseEntity.ok(ApiResponse.ok(penaltyService.getOverdueReport()));
    }
}
