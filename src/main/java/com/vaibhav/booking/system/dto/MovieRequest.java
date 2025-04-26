package com.vaibhav.booking.system.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieRequest {
    private String title;
    private String genre;
    private Integer durationInMinutes;
    private String language;
    private LocalDate releaseDate;
}
