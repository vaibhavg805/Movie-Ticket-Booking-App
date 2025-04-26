package com.vaibhav.booking.system.service;

import com.vaibhav.booking.system.dto.BookingRequest;
import com.vaibhav.booking.system.dto.BookingResponse;

public interface SeatBooking {
    BookingResponse bookSeat(BookingRequest bookingRequest);
}
