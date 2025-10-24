package com.example.cinema_booking.dto.request;

import lombok.Data;

@Data
public class AccountUpdateRequest {
    private String fullName;
    private String phone;
    private String sex;
    private int roleId;
}
