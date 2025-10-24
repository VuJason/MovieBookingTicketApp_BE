package com.example.cinema_booking.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class StaffShiftResponse {
    private Date shiftDate;
    private AccountResponse account;
    private ShiftResponse shift;



}
