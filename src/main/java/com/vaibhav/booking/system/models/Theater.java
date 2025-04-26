package com.vaibhav.booking.system.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String location;
    private Integer totalSeats;

    @OneToMany(mappedBy = "theater",cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Show> shows = new ArrayList<>();
}
