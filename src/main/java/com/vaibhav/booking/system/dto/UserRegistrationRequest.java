package com.vaibhav.booking.system.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String email;
}
