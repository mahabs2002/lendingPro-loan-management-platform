package com.loanapp.audit;

import com.loanapp.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByActorUserIdOrderByCreatedAtDesc(Long userId);
    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);
    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
