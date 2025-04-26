package com.vaibhav.booking.system.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vaibhav.booking.system.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Boolean isBooked = false;

    @ManyToOne
    @JoinColumn(name = "show_id")
    @JsonBackReference
    private Show show;

}
