package com.example.cinema_booking.repository;


import com.example.cinema_booking.model.Booking;
import com.example.cinema_booking.model.Seat;
import com.example.cinema_booking.model.Showtime;
import com.example.cinema_booking.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findBySeatInAndShowtimeAndStatusIn(
            List<Seat> seats, Showtime showtime, List<String> statuses
    );

    List<Ticket> findByStatusAndBookingIsNull(
            String status
    );

    List<Ticket> findBySeatInAndShowtimeAndStatusAndBookingIsNull(
            List<Seat> seats, Showtime showtime, String status
    );

    boolean existsBySeatAndShowtimeAndStatusIn(
            Seat seat, Showtime showtime, List<String> statuses
    );


    List<Ticket> findByStatusAndHeldUntilBeforeAndBookingIsNull(
            String status, LocalDateTime time
    );
    List<Ticket> findByShowtime(Showtime showtime);
    List<Ticket> findBySeatAndShowtimeAndStatusIn(Seat seat, Showtime showtime, List<String> statuses);

}