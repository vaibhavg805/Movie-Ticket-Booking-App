package com.vaibhav.booking.system.dto;

import com.vaibhav.booking.system.enums.BookingStatus;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private BookingStatus status;
    private String message;
}
