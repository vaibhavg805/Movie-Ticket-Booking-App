package com.vaibhav.booking.system.controller;

import com.vaibhav.booking.system.dto.*;
import com.vaibhav.booking.system.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class ShowController {

    private final BookingService showService;

    @PostMapping("/admin/shows")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> createShow(@RequestBody CreateShowRequest request) {
        return ResponseEntity.ok(showService.createShow(request));
    }

    @GetMapping("/shows")
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/shows/{id}/seats")
    public ResponseEntity<List<SeatResponse>> getShowSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowSeats(id));
    }

    @PostMapping("/api/theaters")
    public ResponseEntity<TheaterResponse> createTheater(@RequestBody TheaterRequest theaterRequest) {
        TheaterResponse response = showService.createTheater(theaterRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/api/movies")
    public ResponseEntity<MovieResponse> createMovie(@RequestBody MovieRequest movieRequest) {
        MovieResponse response = showService.createMovie(movieRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}

