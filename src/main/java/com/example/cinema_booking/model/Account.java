package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fullName;
    @JsonIgnore
    private String password;
    private String email;
    private String phone;
    private String sex;
    private String dateOfBirth;

    private String verificationToken;
    private boolean isVerified = false;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "staff")
    private List<StaffShift> staffShifts;
}
