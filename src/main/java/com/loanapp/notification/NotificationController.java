package com.loanapp.notification;

import com.loanapp.auth.UserRepository;
import com.loanapp.common.ApiResponse;
import com.loanapp.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow().getId();
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getMyNotifications(userId)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", null));
    }
}
