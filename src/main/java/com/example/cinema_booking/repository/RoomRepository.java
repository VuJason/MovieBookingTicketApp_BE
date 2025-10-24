package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(Integer roomNumber);
}
