package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.model.Role;
import lombok.Data;

@Data
public class RegisterResponse {
    private int id;
    private String fullName;
    private String password;
    private String email;
    private String phone;
    private String sex;
    private String dateOfBirth;
    private Role role;
}
