package com.example.cinema_booking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowtimeRequest {

    @NotNull(message = "Movie ID is required")
    private Integer movieId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

//    @NotNull(message = "End time is required")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
//    private LocalDateTime endTime;
}