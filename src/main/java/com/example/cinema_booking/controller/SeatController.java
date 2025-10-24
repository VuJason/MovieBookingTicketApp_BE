package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.SeatStatusDTO;
import com.example.cinema_booking.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<SeatStatusDTO>> getSeatsByShowtime(@PathVariable Long showtimeId) {
        List<SeatStatusDTO> seatList = seatService.getSeatsWithStatusByShowtime(showtimeId);
        return ResponseEntity.ok(seatList);
    }
}
