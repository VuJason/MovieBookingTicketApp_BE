package com.example.cinema_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private int id;
    private String maskedEmail;
    private String maskedPhoneNumber;
    private String fullName;
    private String sex;
    private String dateOfBirth;
}
