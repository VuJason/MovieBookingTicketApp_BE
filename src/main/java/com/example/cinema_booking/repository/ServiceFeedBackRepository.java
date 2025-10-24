package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Feedback;
import com.example.cinema_booking.model.ServiceFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceFeedBackRepository extends JpaRepository<ServiceFeedback, Integer> {
    Optional<ServiceFeedback> findById(int id);
}
