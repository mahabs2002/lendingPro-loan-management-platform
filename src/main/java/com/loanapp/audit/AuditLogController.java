package com.loanapp.audit;

import com.loanapp.common.ApiResponse;
import com.loanapp.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auditor/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(auditLogService.getLogsByUser(userId)));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByEntity(
            @PathVariable String entityName, @PathVariable Long entityId) {
        return ResponseEntity.ok(ApiResponse.ok(auditLogService.getLogsByEntity(entityName, entityId)));
    }
}
