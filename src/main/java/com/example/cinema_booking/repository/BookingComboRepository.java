package com.example.cinema_booking.repository;
import com.example.cinema_booking.model.BookingCombo;
import com.example.cinema_booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookingComboRepository extends JpaRepository<BookingCombo, Long> {

    // Lấy tất cả combo của một booking
    List<BookingCombo> findByBooking(Booking booking);

    // Lấy theo bookingId (nếu không có entity Booking sẵn)
    List<BookingCombo> findByBooking_Id(Long bookingId);

    // Tùy chọn: tìm combo cụ thể trong một booking
    BookingCombo findByBooking_IdAndCombo_Id(Long bookingId, Integer comboId);
}
