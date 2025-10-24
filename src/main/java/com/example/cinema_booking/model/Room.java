package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", unique = true, nullable = false)
    private Integer roomNumber;

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, MAINTENANCE

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "seats_per_row", nullable = false)
    private Integer seatsPerRow;
    @ManyToOne
    //@Cascade(CascadeType.ALL)
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

}
