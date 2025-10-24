package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.SeatStatusDTO;
import com.example.cinema_booking.model.Seat;
import com.example.cinema_booking.model.Showtime;
import com.example.cinema_booking.model.Ticket;
import com.example.cinema_booking.repository.SeatRepository;
import com.example.cinema_booking.repository.ShowtimeRepository;
import com.example.cinema_booking.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    /**
     * Trả về danh sách ghế + trạng thái (AVAILABLE, HELD, BOOKED) của 1 suất chiếu
     */
    public List<SeatStatusDTO> getSeatsWithStatusByShowtime(Long showtimeId) {
        // 1. Tìm suất chiếu
        Showtime showtime = showtimeRepository.findById(showtimeId.intValue())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // 2. Lấy danh sách ghế của phòng
        Long roomId = showtime.getRoom().getId();
        List<Seat> seats = seatRepository.findByRoomId(roomId);

        // 3. Lấy tất cả tickets của suất chiếu này
        List<Ticket> tickets = ticketRepository.findByShowtime(showtime);

        // 4. Gộp thông tin ghế + trạng thái
        return seats.stream()
                .map(seat -> {
                    // Mặc định AVAILABLE
                    String status = "AVAILABLE";

                    // Tìm ticket liên quan đến seat
                    Ticket relatedTicket = tickets.stream()
                            .filter(ticket -> ticket.getSeat().getId().equals(seat.getId()))
                            .findFirst()
                            .orElse(null);

                    if (relatedTicket != null) {
                        status = relatedTicket.getStatus(); // HELD hoặc BOOKED
                    }

                    return new SeatStatusDTO(
                            seat.getId(),
                            seat.getSeatRow(),
                            seat.getSeatNumber(),
                            70000.0,
                            status
                    );
                })
                .collect(Collectors.toList());
    }
}
