package com.vaibhav.booking.system.config;

import com.vaibhav.booking.system.models.User;
import com.vaibhav.booking.system.repository.UserRepository;
import com.vaibhav.booking.system.serviceimpl.MfaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MfaService mfaService;

    @Override
    @Transactional
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@example.com");
            adminUser.setRole("ROLE_ADMIN");
            adminUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(adminUser);
            log.info("Created admin user");
        }

        // Create test user with MFA if not exists
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setEmail("test@example.com");
            testUser.setRole("ROLE_USER");
            testUser.setCreatedAt(LocalDateTime.now());

            // Enable MFA for test user
            Map<String,String> secretKeyData = mfaService.generateSecretKey("testuser");
          //  testUser.setOtpSecret(secretKeyData);
            testUser.setMfaEnabled(true);

            User savedUser = userRepository.save(testUser);

            log.info("Created test user with MFA enabled");
          //  log.info("MFA Secret for {}: {}", savedUser.getUsername(), secretKeyData.get("secretKey"));
          //  log.info("MFA QR Code URL: {}", mfaService.generateQrCodeUrl(savedUser.getUsername(), secretKeyData.get("secretKey")));
        }
    }
}
