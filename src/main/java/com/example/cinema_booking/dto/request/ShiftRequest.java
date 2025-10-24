package com.example.cinema_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequest {
    @NotBlank(message = "Tên ca làm việc không được để trống")
    private String name;
    
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private Time startTime;
    
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private Time endTime;
    
    @Min(value = 1, message = "Số lượng nhân viên phải lớn hơn 0")
    private int quantity;
    
    @NotBlank(message = "Màu sắc không được để trống")
    private String color;
}
