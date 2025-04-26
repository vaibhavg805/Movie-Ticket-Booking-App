package com.vaibhav.booking.system.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowResponse {
    private Long showId;
    private String movieTitle;
    private String theaterName;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
