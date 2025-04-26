package com.vaibhav.booking.system.serviceimpl;

import com.vaibhav.booking.system.dto.MfaToggleRequest;
import com.vaibhav.booking.system.dto.UserDto;
import com.vaibhav.booking.system.dto.UserRegistrationRequest;
import com.vaibhav.booking.system.exception.*;
import com.vaibhav.booking.system.models.User;
import com.vaibhav.booking.system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MfaService mfaService;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,
                       MfaService mfaService){
        this.mfaService=mfaService;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    // Cache to store OTP salt values
  //  private final Map<String, String> userSaltCache = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole()))
        );
    }

    @Transactional
    public UserDto registerUser(UserRegistrationRequest registrationRequest) {
        // Validate that username and email are unique
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setEmail(registrationRequest.getEmail());
        user.setMfaEnabled(false);
        user.setRole("ROLE_USER");
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        return mapUserToDto(savedUser);
    }

    @Transactional
    public UserDto enableMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        if (!user.isMfaEnabled()) {
          //  Map<String, String> secretKeyData = mfaService.generateSecretKey(username);
            Map<String,String> secretKey =  mfaService.generateSecretKey(username);

            // Store the encrypted key in the database
           // user.setOtpSecret(secretKey.get("encryptedKey"));
           // user.setMfaEnabled(true);
            // Store the salt in the cache for verification later
          // userSaltCache.put(username, secretKey.get("salt"));
            user.setOtpSecret(secretKey.get("encryptedKey"));
            user.setOtpSalt(secretKey.get("salt"));
            user.setMfaEnabled(true);


            log.info("MFA enabled for user: {}", username);
            User savedUser = userRepository.save(user);

            // We return the plain text secret for initial setup
            UserDto dto = mapUserToDto(savedUser);
            dto.setQrCodeUrl(secretKey.get("qrCodeBase64"));
            return dto;
        }

        // If MFA is already enabled, just return the current user
        return mapUserToDto(user);
    }

    @Transactional
    public UserDto disableMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        if (user.isMfaEnabled()) {
            user.setMfaEnabled(false);
            user.setOtpSecret(null);
            //userSaltCache.remove(username);
            user.setOtpSecret(null);
            user.setOtpSalt(null);
            log.info("MFA disabled for user: {}", username);
            User savedUser = userRepository.save(user);

            return mapUserToDto(savedUser);
        }

        // If MFA is already disabled, just return the current user
        return mapUserToDto(user);
    }

    public UserDto toggleMfa(MfaToggleRequest request) {
        if (request.isEnable()) {
            return enableMfa(request.getUsername());
        } else {
            return disableMfa(request.getUsername());
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public boolean verifyMfaCode(String username, String code) {
        User user = findByUsername(username);
        if (!user.isMfaEnabled()) {
            return true; // MFA not enabled, so verification is successful by default
        }

        // Get the salt from cache
       // String salt = userSaltCache.get(username);
        String salt = user.getOtpSalt();
        if (salt == null) {
            log.error("Salt not found for user: {}", username);
            return false;
        }

        // Decrypt the OTP secret
        String decryptedSecret = mfaService.decryptSecretKey(user.getOtpSecret(), salt);

        // Verify the code
        boolean isValid = mfaService.verifyCode(username,decryptedSecret,code);

        if (isValid) {
            // Update last login date
            user.setLastLoginDate(LocalDateTime.now());
            user.setFailedAttemptCount(0);
            userRepository.save(user);
            log.info("Successful MFA verification for user: {}", username);
        } else {
            // Increment failed attempts
            user.setFailedAttemptCount(user.getFailedAttemptCount() + 1);

            // Lock account after 5 failed attempts
            if (user.getFailedAttemptCount() >= 5) {
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
                log.warn("Account locked for user: {} after multiple failed MFA attempts", username);
            }

            userRepository.save(user);
            log.warn("Failed MFA verification attempt for user: {}", username);
        }

        return isValid;
    }

    @Transactional
    public void updateLoginSuccess(String username) {
        User user = findByUsername(username);
        user.setLastLoginDate(LocalDateTime.now());
        user.setFailedAttemptCount(0);
        userRepository.save(user);
    }

    private UserDto mapUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMfaEnabled(user.isMfaEnabled());
        dto.setLastLoginDate(user.getLastLoginDate());
        dto.setCreatedAt(user.getCreatedAt());

       /* if (user.isMfaEnabled() && userSaltCache.containsKey(user.getUsername())) {
            String salt = userSaltCache.get(user.getUsername());
            String decryptedSecret = mfaService.decryptSecretKey(user.getOtpSecret(), salt);
            dto.setQrCodeUrl(mfaService.generateQrCodeUrl(user.getUsername(), decryptedSecret));
        }*/
        if (user.isMfaEnabled() && user.getOtpSalt() != null) {
            String decryptedSecret = mfaService.decryptSecretKey(user.getOtpSecret(), user.getOtpSalt());
            dto.setQrCodeUrl(mfaService.generateQrCodeUrl(user.getUsername(), decryptedSecret));
        }

        return dto;
    }
}
