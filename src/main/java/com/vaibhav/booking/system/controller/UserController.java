package com.vaibhav.booking.system.controller;

import com.vaibhav.booking.system.dto.UserDto;
import com.vaibhav.booking.system.dto.UserRegistrationRequest;
import com.vaibhav.booking.system.exception.BadRequestException;
import com.vaibhav.booking.system.serviceimpl.MfaService;
import com.vaibhav.booking.system.serviceimpl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private  UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            return userService.registerUser(registrationRequest);
        } catch (BadRequestException e) {
            throw e;
        }
    }

    @PostMapping("/mfa/toggle/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto enableMfa(@PathVariable String username) {
        return userService.enableMfa(username);
    }

    @PostMapping("/disable")
    public ResponseEntity<UserDto> disableMfa(@RequestParam String username) {
        UserDto updatedUser = userService.disableMfa(username);
        return ResponseEntity.ok(updatedUser);
    }
}
