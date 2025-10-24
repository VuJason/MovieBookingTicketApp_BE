package com.example.cinema_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private String roomNumber; // hoặc name nếu bạn dùng tên phòng
    private String type; // VD: "2D", "3D", "IMAX"
}
