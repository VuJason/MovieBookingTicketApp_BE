package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByTitle(String title);
    List<Review> findByTitleContainingIgnoreCase(String title);
}
