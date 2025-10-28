package com.example.cinema_booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowtimeDTO {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private MovieDTO movie;
    private RoomDTO room;
}
