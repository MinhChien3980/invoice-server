package com.example.invoiceserver.repo;

import com.example.invoiceserver.entity.Invoice;
import com.example.invoiceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
