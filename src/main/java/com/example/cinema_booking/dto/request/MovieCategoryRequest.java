package com.example.cinema_booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieCategoryRequest {
    private int movieId;
    private List<Integer> categoryIds;
}
