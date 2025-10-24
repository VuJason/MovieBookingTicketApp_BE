package com.example.cinema_booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class StaffShiftRequest {
    @NotNull(message = "Ngày làm việc không được để trống")
    private Date shiftDate;
}
