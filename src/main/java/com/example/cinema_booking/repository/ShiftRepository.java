package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;


@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findByName(String name);

//    List<Shift> findAll(Pageable page);
    Optional<Shift> findById(int id);
}
