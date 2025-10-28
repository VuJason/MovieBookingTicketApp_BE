package com.example.cinema_booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoldTicketRequest {
    private int showtimeId;
    private List<Long> seatIds;
}

