package com.example.cinema_booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class StaffShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date shiftDate;
    private StaffShiftStatus status;


    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Account staff;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

}
