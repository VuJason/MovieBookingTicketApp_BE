package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.dto.RoomDTO;
import com.example.cinema_booking.dto.ShowtimeDTO;
import com.example.cinema_booking.dto.TicketDTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private String status;
    private LocalDateTime bookingTime;
    private Double totalAmount;
    private String paymentUrl;
    private ShowtimeDTO showtime;  // Thông tin suất chiếu đầy đủ
    private List<TicketDTO> tickets;
    private List<ComboOrderResponseDTO> combos;

}
