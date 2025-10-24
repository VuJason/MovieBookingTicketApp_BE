package com.example.cinema_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatStatusDTO {
    private Long seatId;
    private String seatRow;
    private Integer seatNumber;
    private Double price;
    private String status;

}
