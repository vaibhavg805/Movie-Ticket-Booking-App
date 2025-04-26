package com.vaibhav.booking.system.controller;

import com.vaibhav.booking.system.dto.LoginResponse;
import com.vaibhav.booking.system.dto.MfaVerificationRequest;
import com.vaibhav.booking.system.dto.UserDto;
import com.vaibhav.booking.system.models.User;
import com.vaibhav.booking.system.serviceimpl.UserService;
import com.vaibhav.booking.system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mfa")
@Slf4j
public class MfaController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    public MfaController(JwtUtil jwtUtil, UserService userService){
        this.userService=userService;
        this.jwtUtil=jwtUtil;
    }

    @PostMapping("/verify")
    public ResponseEntity<LoginResponse> verifyMfa(@RequestBody MfaVerificationRequest request) {
        boolean isValid = userService.verifyMfaCode(request.getUsername(), request.getCode());

        LoginResponse response = new LoginResponse();
        response.setMfaRequired(false);

        if (isValid) {
            // Get updated user information
            User user = userService.findByUsername(request.getUsername());
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            userDto.setMfaEnabled(user.isMfaEnabled());
            userDto.setLastLoginDate(user.getLastLoginDate());

            // Create JWT access token for the user after successful MFA
            String accessToken = jwtUtil.generateToken(user.getUsername());

            response.setSuccess(true);
            response.setMessage("MFA verification successful");
            response.setUser(userDto);
            response.setAccessToken(accessToken);

            log.info("MFA verification successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setMessage("Invalid MFA code");

            log.warn("MFA verification failed for user: {}", request.getUsername());
            return ResponseEntity.status(401).body(response);
        }
    }
}
