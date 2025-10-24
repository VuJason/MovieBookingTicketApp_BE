package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Booking booking; // Gộp nhiều vé thành 1 lần đặt

    @ManyToOne
    private Showtime showtime;

    @ManyToOne
    private Seat seat;

    private Double price;

    private String status; // BOOKED /HELD/ CANCELLED (hoặc dùng Enum)

    private LocalDateTime heldUntil;

}
