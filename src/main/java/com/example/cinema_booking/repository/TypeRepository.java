package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

    Optional<Type> findByName(String name);


    boolean existsByName(String name);
}