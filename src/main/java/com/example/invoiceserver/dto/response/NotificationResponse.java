package com.example.invoiceserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private String notificationType;
    private Long invoiceId;
    private String invoiceNumber;
    private boolean read;
    private LocalDateTime createdAt;
} 