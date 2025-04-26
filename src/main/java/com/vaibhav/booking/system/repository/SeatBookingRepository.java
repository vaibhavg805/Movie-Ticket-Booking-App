package com.vaibhav.booking.system.repository;

import com.vaibhav.booking.system.models.Booking;
import com.vaibhav.booking.system.models.Seat;
import com.vaibhav.booking.system.models.Show;
import com.vaibhav.booking.system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatBookingRepository extends JpaRepository<Booking,Long> {

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.show = :show AND b.seat = :seat")
    List<Booking> findByUserAndShowAndSeat(
            @Param("user") User user,
            @Param("show") Show show,
            @Param("seat") Seat seat
    );

}

