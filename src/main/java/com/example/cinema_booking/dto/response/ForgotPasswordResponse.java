package com.example.cinema_booking.dto.response;

import lombok.Data;

@Data
public class ForgotPasswordResponse {
    private String message;
    private boolean success;
    private String email;
} 