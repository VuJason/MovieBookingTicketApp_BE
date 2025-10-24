package com.example.cinema_booking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftAssignmentCountDTO {
    private int shiftId;
    private String shiftName;
    private int assignedStaff;
    private int requiredStaff;

}