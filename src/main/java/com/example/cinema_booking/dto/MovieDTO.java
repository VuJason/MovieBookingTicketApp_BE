package com.example.cinema_booking.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private String director;
    private String actor;
    private LocalDate releaseDate;
    private String trailer;
    private String posterUrl;
}
