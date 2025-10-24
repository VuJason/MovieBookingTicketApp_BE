package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Integer> {
    List<Combo> findByStatus(Boolean status);
    List<Combo> findByNameContainingIgnoreCase(String name);
}