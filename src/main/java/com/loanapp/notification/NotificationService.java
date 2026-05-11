package com.loanapp.notification;

import com.loanapp.auth.UserRepository;
import com.loanapp.entity.*;
import com.loanapp.enums.NotificationType;
import com.loanapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Async
    public void send(Long userId, NotificationType type, String message) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Notification notification = Notification.builder()
                    .user(user)
                    .type(type)
                    .message(message)
                    .status("UNREAD")
                    .build();
            notificationRepository.save(notification);
            log.info("Notification sent to user {}: {}", userId, message);
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }

    public void sendEmiReminder(EmiSchedule emi) {
        Long userId = emi.getLoanAccount().getApplication().getUser().getId();
        String msg = "Reminder: EMI of Rs. " + emi.getEmiAmount()
                + " is due on " + emi.getDueDate()
                + " for loan " + emi.getLoanAccount().getLoanAccountNumber();
        send(userId, NotificationType.EMI_REMINDER, msg);
    }

    public List<Notification> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    public void markRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setStatus("READ");
            notificationRepository.save(n);
        });
    }
}
