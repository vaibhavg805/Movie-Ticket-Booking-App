package com.vaibhav.booking.system.serviceimpl;

import com.vaibhav.booking.system.dto.BookingRequest;
import com.vaibhav.booking.system.dto.BookingResponse;
import com.vaibhav.booking.system.enums.BookingStatus;
import com.vaibhav.booking.system.exception.BookingException;
import com.vaibhav.booking.system.exception.UserNotFoundException;
import com.vaibhav.booking.system.models.Booking;
import com.vaibhav.booking.system.models.Seat;
import com.vaibhav.booking.system.models.Show;
import com.vaibhav.booking.system.models.User;
import com.vaibhav.booking.system.repository.SeatBookingRepository;
import com.vaibhav.booking.system.repository.SeatRepository;
import com.vaibhav.booking.system.repository.ShowRepository;
import com.vaibhav.booking.system.repository.UserRepository;
import com.vaibhav.booking.system.service.SeatBooking;
import com.vaibhav.booking.system.util.SeatLockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatBookingImpl implements SeatBooking {
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatBookingRepository bookingRepository;

    private UserRepository userRepository;

    private final SeatLockManager seatLockManager;

    public SeatBookingImpl(SeatLockManager seatLockManager,UserRepository userRepository){
        this.seatLockManager=seatLockManager;
        this.userRepository=userRepository;
    }

    public BookingResponse bookSeat(BookingRequest request) {
        // Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        try {
            //Retrieve the show
            Show show = showRepository.findById(request.getShowId())
                    .orElseThrow(() -> {
                        saveBookingStatus(user, null, null, BookingStatus.FAILED);
                        return new BookingException("Show Not Found", HttpStatus.NOT_FOUND);
                    });

            // Retrieve the seat
            Seat seat = seatRepository.findByShowAndSeatNumber(show, request.getSeatNumber())
                    .orElseThrow(() -> {
                        saveBookingStatus(user, show, null, BookingStatus.FAILED);
                        return new BookingException("Seat Not Found",HttpStatus.NOT_FOUND);
                    });

            //  if seat is already booked
            if (seat.getIsBooked()) {
                saveBookingStatus(user, show, seat, BookingStatus.FAILED);
                return BookingResponse.builder()
                        .status(BookingStatus.FAILED)
                        .message("Seat is already booked!")
                        .build();
            }

            // Attempt to lock the seat
            if (!seatLockManager.lockSeat(request.getSeatNumber())) {
                saveBookingStatus(user, show, seat, BookingStatus.FAILED);
                return BookingResponse.builder()
                        .status(BookingStatus.FAILED)
                        .message("Seat is currently being booked by another user, please try again!")
                        .build();
            }

            try {
                // I Checkd again to avoid race condition

                seat = seatRepository.findByShowAndSeatNumber(show, request.getSeatNumber())
                        .orElseThrow(() -> new BookingException("Seat Not Found",HttpStatus.NOT_FOUND));

                if (seat.getIsBooked()) {
                    saveBookingStatus(user, show, seat, BookingStatus.FAILED);
                    return BookingResponse.builder()
                            .status(BookingStatus.FAILED)
                            .message("Seat is already booked!")
                            .build();
                }

                // Mark the seat as booked and save
                seat.setIsBooked(true);
                seatRepository.save(seat);

                // Create and save the booking
                Booking booking = new Booking();
                booking.setShow(show);
                booking.setSeat(seat);
                booking.setUser(user);
                booking.setStatus(BookingStatus.SUCCESS);
                bookingRepository.save(booking);

                // Step 9: Return success response
                return BookingResponse.builder()
                        .status(BookingStatus.SUCCESS)
                        .message("Seat successfully booked!")
                        .build();
            } finally {
                //  release the lock
                seatLockManager.unlockSeat(request.getSeatNumber());
            }
        } catch (Exception e) {
            // Handle unexpected exceptions
            saveBookingStatus(user, null, null, BookingStatus.FAILED);
            throw e;
        }
    }


    private void saveBookingStatus(User user, Show show, Seat seat, BookingStatus status) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // For existing bookings matching these criteria
        if (show != null && seat != null) {
            List<Booking> existingBookings = bookingRepository.findByUserAndShowAndSeat(user, show, seat);
            if (!existingBookings.isEmpty()) {
                for (Booking booking : existingBookings) {
                    booking.setStatus(status);
                }
                bookingRepository.saveAll(existingBookings);
                return;
            }
        }

        // Create a new booking with available information
        Booking newBooking = new Booking();
        newBooking.setUser(user);
        if (show != null) newBooking.setShow(show);
        if (seat != null) newBooking.setSeat(seat);
        newBooking.setStatus(status);
        bookingRepository.save(newBooking);
    }

}
