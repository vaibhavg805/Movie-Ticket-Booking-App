package com.vaibhav.booking.system.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private boolean mfaEnabled;
    private String qrCodeUrl;
    private LocalDateTime lastLoginDate;
    private LocalDateTime createdAt;


}
