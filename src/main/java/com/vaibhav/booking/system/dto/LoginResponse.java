package com.vaibhav.booking.system.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private boolean mfaRequired;
    private String message;
    private String accessToken;
    private UserDto user;
}
