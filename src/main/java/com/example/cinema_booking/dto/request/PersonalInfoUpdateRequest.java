package com.example.cinema_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PersonalInfoUpdateRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
} 