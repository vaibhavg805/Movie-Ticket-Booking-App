package com.vaibhav.booking.system.repository;

import com.vaibhav.booking.system.models.Movie;
import com.vaibhav.booking.system.serviceimpl.BookingServiceImpl;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie,Long> {
    Optional<Movie> findById(Long id);
    Optional<Movie> findByTitle(String title);
    boolean existsByTitle(String title);
    @EntityGraph(attributePaths = "shows")
    @Query("SELECT m FROM Movie m")
    List<Movie> findAllMoviesWithShows();
}
