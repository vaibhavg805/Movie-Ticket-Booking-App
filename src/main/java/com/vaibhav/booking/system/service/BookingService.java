package com.vaibhav.booking.system.service;

import com.vaibhav.booking.system.dto.*;

import java.util.List;

public interface BookingService {
   MovieResponse createMovie(MovieRequest movieRequest);
   TheaterResponse createTheater(TheaterRequest theaterRequest);
   ShowResponse createShow(CreateShowRequest createShowRequest);
   List<ShowResponse> getAllShows();
   List<SeatResponse> getShowSeats(Long showId);
}

