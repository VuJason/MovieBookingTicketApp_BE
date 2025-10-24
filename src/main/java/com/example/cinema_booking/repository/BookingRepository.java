package com.example.cinema_booking.repository;


import com.example.cinema_booking.model.Account;
import com.example.cinema_booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 1. Tìm các booking PENDING đã hết hạn (quá 15 phút)
    List<Booking> findByStatusAndBookingTimeBefore(
            String status,
            LocalDateTime time
    );

    // 2. Lấy danh sách booking theo user
    List<Booking> findByUserOrderByBookingTimeDesc(Account user);
}