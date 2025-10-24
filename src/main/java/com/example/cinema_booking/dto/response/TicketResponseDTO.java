package com.example.cinema_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDTO {
    private Long seatId;
    private String seatCode;     // e.g. "A1" (gộp row + number)
    private Double price;

}
