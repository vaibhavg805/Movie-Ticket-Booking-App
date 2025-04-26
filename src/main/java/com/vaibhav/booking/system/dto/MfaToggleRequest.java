package com.vaibhav.booking.system.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaToggleRequest {
    private String username;
    private boolean enable;
}
