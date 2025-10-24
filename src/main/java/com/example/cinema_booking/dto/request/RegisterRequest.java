package com.example.cinema_booking.dto.request;

import com.example.cinema_booking.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    @NotBlank(message = "Password can't blank")
    private String password;
    @Email(message = "Wrong email format")
    private String email;
    private String phone;
    private String sex;
    private String dateOfBirth;
    private Role role;
}
