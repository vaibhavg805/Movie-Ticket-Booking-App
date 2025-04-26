/*
package com.vaibhav.booking.system.security.config;

import com.vaibhav.booking.system.exception.MfaRequiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaibhav.booking.system.models.User;
import com.vaibhav.booking.system.serviceimpl.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MfaAuthenticationFilter extends OncePerRequestFilter {

    private  UserService userService;
    private  ObjectMapper objectMapper;

    @Autowired
    public void setUserService(@Lazy UserService userService){
        this.userService=userService;
    }

    public MfaAuthenticationFilter(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }

    private static final String MFA_HEADER = "X-MFA-OTP";

    // URLs that should bypass MFA authentication
    private static final String[] MFA_BYPASS_URLS = {
            "/api/users/register",
          //  "/api/mfa/verify",
            "/actuator",
            "/api/public"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip MFA for certain URLs
        if (shouldBypassMfa(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If there's no authentication or it's anonymous, skip MFA verification
        if (auth == null || !auth.isAuthenticated() ||
                "anonymousUser".equals(auth.getPrincipal().toString())) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = auth.getName();
        log.debug("Checking MFA requirement for user: {}", username);

        try {
            User user = userService.findByUsername(username);

            // If MFA is not enabled for this user, proceed
            if (!user.isMfaEnabled()) {
                log.debug("MFA not enabled for user: {}, proceeding with request", username);
                filterChain.doFilter(request, response);
                return;
            }

            // Check for OTP code in header
            String otpCode = request.getHeader(MFA_HEADER);

            // If no OTP provided, reject with 401
            if (otpCode == null || otpCode.isEmpty()) {
                log.warn("MFA required but no OTP provided for user: {}", username);
                sendMfaRequiredResponse(response);
                return;
            }

            // Verify OTP
            if (userService.verifyMfaCode(username, otpCode)) {
                // OTP is valid, create a new fully authenticated token
                Collection<GrantedAuthority> authorities = Collections.singleton(
                        new SimpleGrantedAuthority(user.getRole())
                );

                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        auth.getPrincipal(), auth.getCredentials(), authorities
                );

                SecurityContextHolder.getContext().setAuthentication(newAuth);
                log.info("MFA verification successful for user: {}", username);
                filterChain.doFilter(request, response);
            } else {
                // Invalid OTP
                log.warn("Invalid MFA code provided for user: {}", username);
                sendInvalidMfaResponse(response);
            }

        } catch (Exception e) {
            log.error("Error during MFA verification for user: {}", username, e);
            sendErrorResponse(response, e.getMessage());
        }
    }

    private boolean shouldBypassMfa(String requestUri) {
        for (String bypassUrl : MFA_BYPASS_URLS) {
            if (requestUri.startsWith(bypassUrl)) {
                return true;
            }
        }
        return false;
    }

    private void sendMfaRequiredResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("error", "Unauthorized");
        responseBody.put("message", "MFA required. Please provide OTP code in X-MFA-OTP header.");
        responseBody.put("mfaRequired", true);

        objectMapper.writeValue(response.getWriter(), responseBody);
    }

    private void sendInvalidMfaResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("error", "Unauthorized");
        responseBody.put("message", "Invalid MFA code");
        responseBody.put("mfaRequired", true);

        objectMapper.writeValue(response.getWriter(), responseBody);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseBody.put("error", "Internal Server Error");
        responseBody.put("message", "Error during MFA verification: " + message);

        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
*/
