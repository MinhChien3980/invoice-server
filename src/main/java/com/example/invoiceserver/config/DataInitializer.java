package com.example.invoiceserver.config;

import com.example.invoiceserver.entity.User;
import com.example.invoiceserver.entity.UserRole;
import com.example.invoiceserver.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initializeAdminAccount();
    }

    private void initializeAdminAccount() {
        // Kiểm tra xem đã có admin chưa
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Tạo tài khoản admin mặc định...");
            
            Set<UserRole> roles = new HashSet<>();
            roles.add(UserRole.ROLE_ADMIN);
            roles.add(UserRole.ROLE_USER);

            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .fullName("Administrator")
                    .roles(roles)
                    .enabled(true)
                    .build();

            userRepository.save(adminUser);
            log.info("Tài khoản admin đã được tạo: username = admin, password = admin123");
        } else {
            log.info("Tài khoản admin đã tồn tại, không cần tạo mới.");
        }
        
        // Tạo thêm tài khoản user thường nếu chưa có
        if (userRepository.findByUsername("user").isEmpty()) {
            log.info("Tạo tài khoản user thường mặc định...");
            
            Set<UserRole> roles = new HashSet<>();
            roles.add(UserRole.ROLE_USER);

            User normalUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .email("user@example.com")
                    .fullName("Regular User")
                    .roles(roles)
                    .enabled(true)
                    .build();

            userRepository.save(normalUser);
            log.info("Tài khoản user đã được tạo: username = user, password = user123");
        }
    }
} 