package com.vaibhav.booking.system.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private Integer durationInMinutes;
    private String language;
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "movie",cascade = CascadeType.ALL)
     @JsonManagedReference
    List<Show> shows = new ArrayList<>();
}
