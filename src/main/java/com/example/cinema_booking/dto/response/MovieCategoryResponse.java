package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.model.Category;
import com.example.cinema_booking.model.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieCategoryResponse {
    private Movie movie;
    private Category category;
}
