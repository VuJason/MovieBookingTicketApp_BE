package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.model.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private int id;
    private String category;
}
