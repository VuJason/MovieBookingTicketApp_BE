package com.example.cinema_booking.controller;

import com.example.cinema_booking.model.Type;
import com.example.cinema_booking.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roomTypes")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping
    public ResponseEntity<List<Type>> getAllRoomTypes() {
        return ResponseEntity.ok(typeService.getAllTypes());
    }
}