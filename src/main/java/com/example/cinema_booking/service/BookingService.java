package com.example.cinema_booking.service;


import com.example.cinema_booking.dto.request.BookingRequestDTO;
import com.example.cinema_booking.dto.request.HoldTicketRequest;
import com.example.cinema_booking.model.Booking;
import com.example.cinema_booking.model.Ticket;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingRequestDTO dto);
    List<Ticket> holdTickets(HoldTicketRequest request);
    void cancelHeldTickets(HoldTicketRequest request);
    Booking confirmBookingPayment(Long bookingId);
    Booking cancelBooking(Long bookingId);
    Booking getBookingById(Long bookingId);
    List<Booking> getMyBookings();
    boolean isBookingValidForPayment(Long bookingId);
}
