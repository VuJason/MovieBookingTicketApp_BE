package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.model.Role;
import lombok.Data;

@Data
public class AccountResponse {
    private int accountId;
    private String accountName;
    private String email;
    private Role role;
}
