package com.example.cinema_booking.dto.response;

import lombok.Data;
import java.sql.Time;
import java.util.Date;

@Data
public class ShiftDetailResponse {
    private Shift shift;
    private Date shiftDate;

    @Data
    public static class Shift {
        private int id;
        private String name;
        private Time startTime;
        private Time endTime;
        private int required_staff;
        private String color;
    }
}
