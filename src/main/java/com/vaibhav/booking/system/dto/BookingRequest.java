package com.vaibhav.booking.system.dto;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Long userId;
    private Long showId;
    private String seatNumber;
}
