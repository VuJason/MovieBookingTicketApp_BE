package com.example.cinema_booking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.cinema_booking.model.Movie;
import com.example.cinema_booking.model.Room;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowtimeResponseDTO {

    private Integer id;

    private Movie movie;

    private Room room;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
}
