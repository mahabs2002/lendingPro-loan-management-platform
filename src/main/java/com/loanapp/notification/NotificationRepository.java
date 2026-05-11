package com.loanapp.notification;

import com.loanapp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
    List<Notification> findByUserIdAndStatus(Long userId, String status);
    long countByUserIdAndStatus(Long userId, String status);
}
