package com.vaibhav.booking.system.controller;

import com.vaibhav.booking.system.dto.BookingRequest;
import com.vaibhav.booking.system.dto.BookingResponse;
import com.vaibhav.booking.system.service.SeatBooking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seat/")
public class SeatBookingController {
    private final SeatBooking seatBooking;
    public SeatBookingController(SeatBooking seatBooking){
        this.seatBooking=seatBooking;
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookSeat(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(seatBooking.bookSeat(bookingRequest));
    }
}
