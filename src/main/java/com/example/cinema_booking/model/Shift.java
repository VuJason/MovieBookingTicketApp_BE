package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.security.Timestamp;
import java.sql.Time;
import java.util.List;

@Entity
@Data
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Time startTime;
    private Time endTime;
    private int required_staff;
    private String color;

    @OneToMany(mappedBy = "shift")
    @JsonIgnore
    private List<StaffShift> staffShiftList;
}
