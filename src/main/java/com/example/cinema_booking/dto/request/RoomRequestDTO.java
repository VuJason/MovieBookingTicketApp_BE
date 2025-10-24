package com.example.cinema_booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDTO {

    @NotNull(message = "Room number is required")
    @Min(value = 1, message = "Room number must be at least 1")
    private Integer roomNumber;

    @NotBlank(message = "Status is required")
    private String status; // e.g., ACTIVE, MAINTENANCE

    @NotNull(message = "Total rows is required")
    @Min(value = 1, message = "There must be at least 1 row")
    private Integer totalRows;

    @NotNull(message = "Seats per row is required")
    @Min(value = 1, message = "Each row must have at least 1 seat")
    private Integer seatsPerRow;

    @NotNull(message = "Type ID is required")
    private Long typeId;
}