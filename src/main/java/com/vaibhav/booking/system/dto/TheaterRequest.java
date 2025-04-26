package com.vaibhav.booking.system.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TheaterRequest {
    private String name;
    private String location;
    private Integer totalSeats;
}
