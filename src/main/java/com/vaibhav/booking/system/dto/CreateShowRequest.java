package com.vaibhav.booking.system.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateShowRequest {
    private Long movieId;
    private Long theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
