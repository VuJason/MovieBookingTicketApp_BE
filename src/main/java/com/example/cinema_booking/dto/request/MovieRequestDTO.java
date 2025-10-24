package com.example.cinema_booking.dto.request;

import com.example.cinema_booking.model.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequestDTO {
    @NotBlank(message = "Movie name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Duration cannot be null")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer duration;

    @NotBlank(message = "Director cannot be blank")
    private String director;

    @NotBlank(message = "Actor cannot be blank")
    private String actor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private String trailer;
    private String posterUrl;
    private Boolean isComingSoon = false;
    @Column(name = "end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private List<Integer> categoryIds;
}