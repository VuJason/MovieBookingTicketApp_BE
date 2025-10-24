package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Optional<Event> findByTitle(String title);
    List<Event> findByTitleIgnoreCase(String title);
}
