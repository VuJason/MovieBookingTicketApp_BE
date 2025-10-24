package com.example.cinema_booking.service;

import com.example.cinema_booking.model.Room;
import com.example.cinema_booking.dto.request.RoomRequestDTO;

import java.util.List;

public interface RoomService {
    Room createRoom(RoomRequestDTO request);

    List<Room> getAllRooms();

    Room getRoomById(Long id);

    Room updateRoom(Long id, RoomRequestDTO request);

    void deleteRoom(Long id);
}

