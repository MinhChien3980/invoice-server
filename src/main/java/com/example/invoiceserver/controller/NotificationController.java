package com.example.invoiceserver.controller;

import com.example.invoiceserver.dto.response.NotificationResponse;
import com.example.invoiceserver.entity.User;
import com.example.invoiceserver.service.NotificationService;
import com.example.invoiceserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<NotificationResponse> notifications = notificationService.getUserNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        NotificationResponse notification = notificationService.markAsRead(id, user);
        return ResponseEntity.ok(notification);
    }
    
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markAllNotificationsAsRead(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().body("All notifications marked as read");
    }
    
    @PostMapping("/test-websocket")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testWebSocket() {
        // Tạo một thông báo test
        NotificationResponse testNotification = NotificationResponse.builder()
                .id(-1L) // ID tạm thời
                .message("Đây là thông báo test từ admin")
                .notificationType("TEST_NOTIFICATION")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Gửi thông báo qua WebSocket
        messagingTemplate.convertAndSend("/topic/notifications", testNotification);
        
        return ResponseEntity.ok().body(Map.of(
            "success", true,
            "message", "Đã gửi thông báo test thành công qua WebSocket"
        ));
    }
} 