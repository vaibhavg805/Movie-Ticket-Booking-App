package com.vaibhav.booking.system.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaVerificationRequest {
    private String username;
    private String code;
}
