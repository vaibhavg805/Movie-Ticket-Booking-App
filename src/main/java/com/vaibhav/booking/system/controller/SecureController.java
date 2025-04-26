package com.vaibhav.booking.system.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getSecureData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is secure data");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminData() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is admin-only data");
        return ResponseEntity.ok(response);
    }
}
