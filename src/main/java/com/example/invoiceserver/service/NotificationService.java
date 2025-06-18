package com.example.invoiceserver.service;

import com.example.invoiceserver.dto.response.NotificationResponse;
import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.entity.Notification;
import com.example.invoiceserver.entity.User;
import com.example.invoiceserver.repo.NotificationRepository;
import com.example.invoiceserver.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void createAndSendInvoiceStatusNotification(Invoice invoice, String message, String notificationType) {
        // First, find all users to notify
        List<User> usersToNotify = userRepository.findAll();
        
        // Lưu thông báo một lần và gửi tới tất cả users
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        // Không gắn với recipient cụ thể
        notification.setInvoiceId(invoice.getId());
        notification.setInvoiceNumber(invoice.getInvoiceNumber());
            
        // Lưu vào database
        notification = notificationRepository.save(notification);
            
        // Convert to DTO
        NotificationResponse notificationResponse = convertToDTO(notification);

        // Chỉ gửi tới topic chung, không gửi tới user-specific topic
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/notifications", notificationResponse);
        }
    }
    
    public List<NotificationResponse> getUserNotifications(User user) {
        // Trả về tất cả thông báo vì không còn liên kết user-specific
        List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        
        return convertToDTO(notification);
    }
    
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByReadOrderByCreatedAtDesc(false);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }
    
    private NotificationResponse convertToDTO(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .invoiceId(notification.getInvoiceId())
                .invoiceNumber(notification.getInvoiceNumber())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
} 