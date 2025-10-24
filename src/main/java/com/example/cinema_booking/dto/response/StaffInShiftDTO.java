package com.example.cinema_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffInShiftDTO {
    private int accountId;
    private String fullName;
    private String email;
} 