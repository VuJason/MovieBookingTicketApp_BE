package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.exception.EntityNotFoundException;
import com.example.cinema_booking.model.Room;
import com.example.cinema_booking.model.Seat;
import com.example.cinema_booking.model.Type;
import com.example.cinema_booking.repository.RoomRepository;
import com.example.cinema_booking.repository.SeatRepository;
import com.example.cinema_booking.repository.TypeRepository;
import com.example.cinema_booking.dto.request.RoomRequestDTO;
import com.example.cinema_booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private SeatRepository seatRepository;

    public Room createRoom(RoomRequestDTO request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setStatus(request.getStatus());
        room.setTotalRows(request.getTotalRows());
        room.setSeatsPerRow(request.getSeatsPerRow());

        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Type not found"));
        room.setType(type);

        Room savedRoom = roomRepository.save(room);
        generateSeats(savedRoom);
        return savedRoom;
    }

    private void generateSeats(Room room) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < room.getTotalRows(); i++) {
            char rowChar = (char) ('A' + i);
            for (int j = 1; j <= room.getSeatsPerRow(); j++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatRow(String.valueOf(rowChar));
                seat.setSeatNumber(j);
                seat.setIsActive(true);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
    }

    public Room updateRoom(Long id, RoomRequestDTO request) {
        Room room = getRoomById(id);

        boolean rowsChanged = !room.getTotalRows().equals(request.getTotalRows());
        boolean seatsChanged = !room.getSeatsPerRow().equals(request.getSeatsPerRow());

        room.setRoomNumber(request.getRoomNumber());
        room.setStatus(request.getStatus());
        room.setTotalRows(request.getTotalRows());
        room.setSeatsPerRow(request.getSeatsPerRow());

        Room updatedRoom = roomRepository.save(room);

        // Nếu thay đổi cấu trúc chỗ ngồi thì xoá hết seat cũ rồi tạo lại
        if (rowsChanged || seatsChanged) {
            seatRepository.deleteAll(room.getSeats()); // Xoá ghế cũ
            generateSeats(updatedRoom); // Tạo lại ghế mới
        }

        return updatedRoom;
    }


    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}
