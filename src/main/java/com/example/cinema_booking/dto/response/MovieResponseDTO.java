package com.example.cinema_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponseDTO {
    private int id;
    private String name;
    private String description;
    private int duration;
    private String director;
    private String actor;
    private LocalDate releaseDate;
    private String trailer;
    private String posterUrl;
    private Boolean isComingSoon;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> categoryNames;
}
