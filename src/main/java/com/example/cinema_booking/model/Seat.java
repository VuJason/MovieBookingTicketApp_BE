package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "seat_row", nullable = false)
    private String seatRow; // A, B, C...

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 1, 2, 3...

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
