package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Room;
import com.example.cinema_booking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomId(Long roomId);
    List<Seat> findByRoomIdAndIsActiveTrue(Long roomId);
    List<Seat> findByIdIn(List<Long> ids);

    List<Seat> findByRoom(Room room);
}
