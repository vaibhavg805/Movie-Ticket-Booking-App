package com.vaibhav.booking.system.repository;

import com.vaibhav.booking.system.models.Show;
import com.vaibhav.booking.system.models.Theater;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show,Long> {
    Optional<Show> findById(Long id);

    @EntityGraph(attributePaths = "seats")
    @Query("SELECT s FROM Show s")
    List<Show> findAllShowsAvailableSeats();
}
