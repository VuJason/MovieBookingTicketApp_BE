package com.example.cinema_booking.dto.request;

import com.example.cinema_booking.dto.ComboOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
//    private Long userId;
    private int showtimeId;

    private List<Long> seatIds;

    private List<ComboOrderDTO> combos; // Nếu có combo

}
