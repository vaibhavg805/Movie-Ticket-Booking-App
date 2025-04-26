package com.vaibhav.booking.system.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "mfa_enabled")
    private boolean mfaEnabled = false;

    @Column(name = "otp_secret", length = 255)
    private String otpSecret;

    @Column(name = "role", nullable = false)
    private String role = "ROLE_USER"; // Default role

    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = true;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "failed_attempt_count")
    private int failedAttemptCount = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "otp_salt")
    private String otpSalt;

}
