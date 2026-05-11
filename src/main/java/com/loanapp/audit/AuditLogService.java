package com.loanapp.audit;

import com.loanapp.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(Long actorId, String action, String entityName,
                    Long entityId, String oldValue, String newValue, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorUserId(actorId)
                    .actionType(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    public List<AuditLog> getLogsByUser(Long userId) {
        return auditLogRepository.findByActorUserIdOrderByCreatedAtDesc(userId);
    }

    public List<AuditLog> getLogsByEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }
}
