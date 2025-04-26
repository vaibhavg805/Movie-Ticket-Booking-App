package com.vaibhav.booking.system.mapping;

import com.vaibhav.booking.system.dto.MovieResponse;
import com.vaibhav.booking.system.dto.ShowResponse;
import com.vaibhav.booking.system.models.Movie;
import com.vaibhav.booking.system.models.Show;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public MovieResponse convertMovieToMovieResponse(Movie movie){
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .message("Movie Inserted Successfully...")
                .build();
    }

    public ShowResponse mapToResponse(Show show){
            return ShowResponse.builder()
                    .showId(show.getId())
                    .movieTitle(show.getMovie().getTitle())
                    .theaterName(show.getTheater().getName())
                    .location(show.getTheater().getLocation())
                    .startTime(show.getStartTime())
                    .endTime(show.getEndTime())
                    .build();
    }

}
