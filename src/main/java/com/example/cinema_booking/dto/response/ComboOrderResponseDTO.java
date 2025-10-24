package com.example.cinema_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboOrderResponseDTO {
    private String name;
    private Double price;
    private Integer quantity;
}
