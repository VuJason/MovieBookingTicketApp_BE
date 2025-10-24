package com.example.cinema_booking.dto;

import lombok.Data;

@Data
public class TicketDTO {
    private Long seatId;
    private String seatCode; // A1, B5,...
    private Double price;
}
