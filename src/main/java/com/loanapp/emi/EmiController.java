package com.loanapp.emi;

import com.loanapp.common.ApiResponse;
import com.loanapp.entity.EmiSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class EmiController {

    private final EmiSchedulerService emiSchedulerService;

    @GetMapping("/{loanAccountId}/emi-schedule")
    public ResponseEntity<ApiResponse<List<EmiScheduleResponse>>> getSchedule(@PathVariable Long loanAccountId) {
        return ResponseEntity.ok(ApiResponse.ok(emiSchedulerService.getSchedule(loanAccountId)));
    }

    @GetMapping("/{loanAccountId}/next-emi")
    public ResponseEntity<ApiResponse<EmiScheduleResponse>> getNextDue(@PathVariable Long loanAccountId) {
        return ResponseEntity.ok(ApiResponse.ok(emiSchedulerService.getNextDue(loanAccountId)));
    }
}
