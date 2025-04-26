package com.vaibhav.booking.system.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vaibhav.booking.system.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime bookingTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_id")
    @JsonBackReference
    private Show show;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    @JsonBackReference
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

