package com.vaibhav.booking.system.serviceimpl;

import com.vaibhav.booking.system.mapping.Mapper;
import com.vaibhav.booking.system.dto.*;
import com.vaibhav.booking.system.enums.SeatType;
import com.vaibhav.booking.system.exception.BookingException;
import com.vaibhav.booking.system.exception.LocationCountExceedsException;
import com.vaibhav.booking.system.exception.MovieAlreadyExistException;
import com.vaibhav.booking.system.exception.TheaterAlreadyExistsException;
import com.vaibhav.booking.system.models.Movie;
import com.vaibhav.booking.system.models.Seat;
import com.vaibhav.booking.system.models.Show;
import com.vaibhav.booking.system.models.Theater;
import com.vaibhav.booking.system.repository.MovieRepository;
import com.vaibhav.booking.system.repository.ShowRepository;
import com.vaibhav.booking.system.repository.TheaterRepository;
import com.vaibhav.booking.system.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class BookingServiceImpl implements BookingService {
    private final Long FIXED_LOCATION_COUNT = 2L;
    private final MovieRepository movieRepository;
    private final Mapper mapper;
    private final TheaterRepository theaterRepository;
    private final ShowRepository showRepository;
    public BookingServiceImpl(MovieRepository movieRepository,Mapper mapper,
                              TheaterRepository theaterRepository,ShowRepository showRepository){
        this.mapper=mapper;
        this.movieRepository=movieRepository;
        this.theaterRepository=theaterRepository;
        this.showRepository=showRepository;
    }

    @Override
    public MovieResponse createMovie(MovieRequest movieRequest) {
        String title = movieRequest.getTitle().trim();
        if(movieRepository.existsByTitle(title)){
            throw new MovieAlreadyExistException("Movie Already Exists...");
        }

      Movie movie =  Movie.builder()
                .title(title)
                .genre(movieRequest.getGenre())
                .durationInMinutes(movieRequest.getDurationInMinutes())
                .language(movieRequest.getLanguage())
                .releaseDate(movieRequest.getReleaseDate())
                .build();
       Movie savedData = movieRepository.save(movie);
        return mapper.convertMovieToMovieResponse(savedData);
    }

    @Override
    public TheaterResponse createTheater(TheaterRequest theaterRequest) {
        String name = theaterRequest.getName().trim();
        if (theaterRepository.existsByName(name)){
            throw new TheaterAlreadyExistsException("Theater Already Exists...");
        }

        if(theaterRepository.countTheatersByLocation(theaterRequest.getLocation().trim()) >= FIXED_LOCATION_COUNT){
            throw new LocationCountExceedsException("Only 2 Theater at a location is allowed");
        }

        Theater theater = Theater.builder()
                .name(name)
                .location(theaterRequest.getLocation())
                .totalSeats(theaterRequest.getTotalSeats())
                .build();
            Theater savedData = theaterRepository.save(theater);

        return TheaterResponse.builder()
                .id(savedData.getId())
                .message("Theater Data Saved Successfully...")
                .build();

    }

    @Override
    public ShowResponse createShow(CreateShowRequest createShowRequest) {
        Movie movie = movieRepository.findById(createShowRequest.getMovieId())
                .orElseThrow(() -> new BookingException("Movie not found", HttpStatus.NOT_FOUND));

        Theater theater = theaterRepository.findById(createShowRequest.getTheaterId())
                .orElseThrow(() -> new BookingException("Theater not found",HttpStatus.NOT_FOUND));

        Show show = Show.builder()
                .movie(movie)
                .theater(theater)
                .startTime(createShowRequest.getStartTime())
                .endTime(createShowRequest.getEndTime())
                .build();
        List<Seat> listSeats = generateSeats(show,theater.getTotalSeats());
            show.setSeats(listSeats);

      Show savedData = showRepository.saveAndFlush(show);
        return mapper.mapToResponse(savedData);
    }

    @Override
    public List<ShowResponse> getAllShows() {
       return  showRepository.findAll().stream()
                .map(show -> ShowResponse.builder()
                            .showId(show.getId())
                            .movieTitle(show.getMovie().getTitle())
                            .theaterName(show.getTheater().getName())
                            .location(show.getTheater().getLocation())
                            .startTime(show.getStartTime())
                            .endTime(show.getEndTime())
                            .build()
                ).collect(Collectors.toList());
    }

    public List<SeatResponse> getShowSeats(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new BookingException("Show not found",HttpStatus.NOT_FOUND));

        return show.getSeats().stream().map(seat -> {
            SeatResponse res = new SeatResponse();
            res.setSeatNumber(seat.getSeatNumber());
            res.setSeatType(seat.getSeatType());
            res.setIsBooked(seat.getIsBooked());
            return res;
        }).collect(Collectors.toList());
    }

    private List<Seat> generateSeats(Show show, int totalSeats) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("A" + i);
            seat.setSeatType(i <= 10 ? SeatType.VIP : SeatType.REGULAR);
            seat.setShow(show);
            seats.add(seat);
        }
        return seats;
    }
}
