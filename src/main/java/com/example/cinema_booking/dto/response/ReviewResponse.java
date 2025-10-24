package com.example.cinema_booking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private int id;
    private String title;
    private String reviewDescription;
    private LocalDate createDate;
    private String author;
    private String reviewPosterUrl;
}
