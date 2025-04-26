package com.vaibhav.booking.system.controller;

import com.vaibhav.booking.system.dto.LoginRequest;
import com.vaibhav.booking.system.dto.LoginResponse;
import com.vaibhav.booking.system.dto.UserDto;
import com.vaibhav.booking.system.models.User;
import com.vaibhav.booking.system.serviceimpl.UserService;
import com.vaibhav.booking.system.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil,AuthenticationManager authenticationManager,UserService userService){
        this.userService=userService;
        this.jwtUtil=jwtUtil;
        this.authenticationManager=authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Authenticate with username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userService.findByUsername(loginRequest.getUsername());
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            userDto.setMfaEnabled(user.isMfaEnabled());

            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setUser(userDto);

            // Check if MFA is required
            if (user.isMfaEnabled()) {
                // MFA is enabled => issue a temporary token
                String tempToken = jwtUtil.generateTemporaryToken(user.getUsername());
                response.setAccessToken(tempToken);
                response.setMfaRequired(true);
                response.setMessage("MFA required. Temporary token issued.");
                log.info("Login success for {}, MFA required", user.getUsername());
            } else {
                // Update last login time for non-MFA users
                // MFA is disabled => issue full access + refresh tokens
                String accessToken = jwtUtil.generateToken(user.getUsername());
                userService.updateLoginSuccess(user.getUsername());
                response.setAccessToken(accessToken);
                response.setMfaRequired(false);
                response.setMessage("Login successful");
                log.info("Login success for {} without MFA", user.getUsername());
            }

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Login failed for username: {}", loginRequest.getUsername());
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMfaRequired(false);
            response.setMessage("Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}
