package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ int thành Long để khớp với typeId

    @Column(nullable = false)
    private String name;

//    @Column(nullable = false)
//    private Integer capacity;

    @OneToMany(mappedBy = "type") // Sửa từ "types" thành "type" để khớp với field trong Room
    @JsonIgnore
    private List<Room> rooms;
}
