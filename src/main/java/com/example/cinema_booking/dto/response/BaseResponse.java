package com.example.cinema_booking.dto.response;

import lombok.Data;

@Data
public class BaseResponse {
    private int code;
    private String message;
    private Object data;
}
