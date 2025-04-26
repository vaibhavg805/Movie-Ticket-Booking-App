package com.vaibhav.booking.system.dto;

import com.vaibhav.booking.system.enums.SeatType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatResponse {
    private String seatNumber;
    private SeatType seatType;
    private Boolean isBooked;
}

