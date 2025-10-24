package com.example.cinema_booking.dto.request;

import com.example.cinema_booking.model.Account;
import com.example.cinema_booking.model.Movie;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FeedbackRequest {
    private int movieId;
    private int accountId;
    private String comment;

    @Min(value = 1, message = "Must be at least 1 star")
    @Max(value = 5, message = "Must be at most 5 stars")
    private int star;
}
