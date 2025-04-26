package com.vaibhav.booking.system.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String message;
}
