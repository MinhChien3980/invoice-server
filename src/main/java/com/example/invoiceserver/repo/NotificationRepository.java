package com.example.invoiceserver.repo;

import com.example.invoiceserver.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Các phương thức tìm kiếm theo thứ tự thời gian giảm dần
    List<Notification> findAllByOrderByCreatedAtDesc();
    List<Notification> findByReadOrderByCreatedAtDesc(boolean read);
} 