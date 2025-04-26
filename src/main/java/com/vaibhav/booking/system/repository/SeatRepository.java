package com.vaibhav.booking.system.repository;

import com.vaibhav.booking.system.models.Booking;
import com.vaibhav.booking.system.models.Seat;
import com.vaibhav.booking.system.models.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    Optional<Seat> findById(Long id);

    @Query("SELECT b FROM Seat b WHERE b.show = :show AND b.seatNumber = :seatNumber")
    Optional<Seat> findByShowAndSeatNumber(@Param("show") Show show, @Param("seatNumber") String seatNumber);

}
