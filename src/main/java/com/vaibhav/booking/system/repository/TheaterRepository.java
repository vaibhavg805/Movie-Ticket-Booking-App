package com.vaibhav.booking.system.repository;

import com.vaibhav.booking.system.models.Theater;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater,Long> {
    Optional<Theater> findById(Long id);
    boolean existsByName(String name);
    boolean existsByLocation(String location);

    @Query("SELECT COUNT(t) FROM Theater t WHERE t.location = :location")
    Long countTheatersByLocation(@Param("location") String location);

    @EntityGraph(attributePaths = "shows")
    @Query("SELECT t FROM Theater t")
    List<Theater> findAllTheaterWithShows();
}
