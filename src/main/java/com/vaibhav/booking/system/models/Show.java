package com.vaibhav.booking.system.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "`show`")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    @JsonBackReference
    private Theater theater;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "show",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();

}
