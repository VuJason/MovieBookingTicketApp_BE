//package com.example.cinema_booking.controller;
//
//import com.example.cinema_booking.dto.RoomDTO;
//import com.example.cinema_booking.dto.TicketDTO;
//import com.example.cinema_booking.dto.request.BookingRequestDTO;
//import com.example.cinema_booking.dto.request.HoldTicketRequest;
//import com.example.cinema_booking.dto.response.BookingResponseDTO;
//import com.example.cinema_booking.dto.response.ComboOrderResponseDTO;
//import com.example.cinema_booking.model.Booking;
//import com.example.cinema_booking.model.Room;
//import com.example.cinema_booking.model.Showtime;
//import com.example.cinema_booking.model.Ticket;
//
//import com.example.cinema_booking.service.BookingService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import vn.payos.PayOS;
//import vn.payos.type.ItemData;
//import vn.payos.type.PaymentData;
//import vn.payos.type.CheckoutResponseData;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
//    @RestController
//    @RequestMapping("/api/bookings")
//    @RequiredArgsConstructor
//    public class BookingController {
//
//        private final BookingService bookingService;
//        private final PayOS payOS;
//
//        /**
//         * API tạo booking - CHỈ tạo booking, KHÔNG tạo PayOS link
//         */
//        @PostMapping
//        public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO requestDTO) {
//            try {
//                // Validate input
//                if (
//                        requestDTO.getShowtimeId() <= 0 ||
//                        requestDTO.getSeatIds() == null ||
//                        requestDTO.getSeatIds().isEmpty()) {
//
//                    return ResponseEntity.badRequest().body("Missing required fields: userId, showtimeId, seatIds");
//                }
//
//                // Tạo booking
//                Booking booking = bookingService.createBooking(requestDTO);
//
//                // Trả về thông tin booking
//                BookingResponseDTO responseDTO = buildBookingResponseDTO(booking);
//                return ResponseEntity.ok(responseDTO);
//
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage(),
//                        "timestamp", LocalDateTime.now()
//                ));
//            } catch (Exception e) {
//                return ResponseEntity.internalServerError().body(Map.of(
//                        "error", "Internal server error: " + e.getMessage(),
//                        "timestamp", LocalDateTime.now()
//                ));
//            }
//        }
//
//        /**
//         * API tạo link thanh toán PayOS cho booking đã tồn tại
//         */
//        @PostMapping("/{bookingId}/payment-link")
//        public ResponseEntity<?> createPaymentLink(@PathVariable Long bookingId) {
//            try {
//                // Validate booking
//                if (!bookingService.isBookingValidForPayment(bookingId)) {
//                    return ResponseEntity.badRequest().body(Map.of(
//                            "error", "Booking không hợp lệ hoặc đã hết hạn thanh toán",
//                            "bookingId", bookingId
//                    ));
//                }
//
//                Booking booking = bookingService.getBookingById(bookingId);
//
//                // Tạo PayOS payment data
//                ItemData item = ItemData.builder()
//                        .name("Đặt vé xem phim - Booking #" + booking.getId())
//                        .price(booking.getTotalAmount().intValue())
//                        .quantity(1)
//                        .build();
//
//                PaymentData paymentData = PaymentData.builder()
//                        .orderCode(booking.getId())
//                        .amount(booking.getTotalAmount().intValue())
//                        .description("Dat ve xem phim - Booking #" + booking.getId())
//                        .returnUrl("http://localhost:5173/payment/success?bookingId=" + booking.getId())
//                        .cancelUrl("http://localhost:5173/payment/cancel?bookingId=" + booking.getId())
//                        .item(item)
//                        .build();
//
//                CheckoutResponseData checkout = payOS.createPaymentLink(paymentData);
//
//                return ResponseEntity.ok(Map.of(
//                        "paymentUrl", checkout.getCheckoutUrl(),
//                        "bookingId", booking.getId(),
//                        "amount", booking.getTotalAmount(),
//                        "status", "PAYMENT_LINK_CREATED",
//                        "expiresAt", booking.getBookingTime().plusMinutes(15)
//                ));
//
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage(),
//                        "bookingId", bookingId
//                ));
//            } catch (Exception e) {
//                return ResponseEntity.internalServerError().body(Map.of(
//                        "error", "Lỗi tạo link thanh toán: " + e.getMessage(),
//                        "bookingId", bookingId
//                ));
//            }
//        }
//
//        /**
//         * API webhook xử lý kết quả thanh toán từ PayOS
//         */
//        @PostMapping("/webhook/payos")
//        public ResponseEntity<?> handlePayOSWebhook(@RequestBody Map<String, Object> webhookData) {
//            try {
//                String status = (String) webhookData.get("status");
//                Long bookingId = Long.valueOf(webhookData.get("orderCode").toString());
//
//                System.out.println("PayOS Webhook - BookingId: " + bookingId + ", Status: " + status);
//
//                if ("PAID".equals(status) || "SUCCESS".equals(status)) {
//                    // ✅ Xác nhận payment + gửi mail
//                    bookingService.confirmBookingPayment(bookingId);
//                    System.out.println("Booking " + bookingId + " payment confirmed & email sent successfully via webhook");
//                } else if ("CANCELLED".equals(status) || "FAILED".equals(status)) {
//                    bookingService.cancelBooking(bookingId);
//                    System.out.println("Booking " + bookingId + " cancelled due to payment " + status);
//                }
//
//                return ResponseEntity.ok("OK");
//            } catch (Exception e) {
//                System.err.println("PayOS Webhook processing failed: " + e.getMessage());
//                e.printStackTrace();
//                return ResponseEntity.internalServerError().body("Webhook processing failed");
//            }
//        }
//        /**
//         * API xác nhận thanh toán thành công (Manual - backup cho webhook)
//         */
//        @PutMapping("/{bookingId}/confirm")
//        public ResponseEntity<?> confirmBookingPayment(@PathVariable Long bookingId) {
//            try {
//                Booking booking = bookingService.confirmBookingPayment(bookingId);
//                return ResponseEntity.ok(Map.of(
//                        "message", "Đã xác nhận thanh toán thành công",
//                        "bookingId", booking.getId(),
//                        "status", booking.getStatus(),
//                        "confirmedAt", LocalDateTime.now()
//                ));
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage(),
//                        "bookingId", bookingId
//                ));
//            }
//        }
//
//        /**
//         * API hủy booking
//         */
//        @PutMapping("/{bookingId}/cancel")
//        public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
//            try {
//                Booking booking = bookingService.cancelBooking(bookingId);
//                return ResponseEntity.ok(Map.of(
//                        "message", "Đã hủy booking và giải phóng ghế",
//                        "bookingId", booking.getId(),
//                        "status", booking.getStatus(),
//                        "cancelledAt", LocalDateTime.now()
//                ));
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage(),
//                        "bookingId", bookingId
//                ));
//            }
//        }
//
//        /**
//         * API lấy thông tin booking
//         */
//        @GetMapping("/{bookingId}")
//        public ResponseEntity<?> getBooking(@PathVariable Long bookingId) {
//            try {
//                Booking booking = bookingService.getBookingById(bookingId);
//                BookingResponseDTO responseDTO = buildBookingResponseDTO(booking);
//                return ResponseEntity.ok(responseDTO);
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage(),
//                        "bookingId", bookingId
//                ));
//            }
//        }
//
//        /**
//         * API lấy tất cả booking của user
//         */
//        @GetMapping("/history/{userId}")
//        public ResponseEntity<List<Booking>> getMyBookings() {
//            List<Booking> bookings = bookingService.getMyBookings();
//            return ResponseEntity.ok(bookings);
//        }
//
//        /**
//         * API giữ ghế tạm thời (không tạo booking)
//         */
//        @PostMapping("/hold")
//        public ResponseEntity<?> holdTickets(@RequestBody HoldTicketRequest request) {
//            try {
//                if (request.getShowtimeId() == null || request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
//                    return ResponseEntity.badRequest().body("Missing required fields: showtimeId, seatIds");
//                }
//
//                List<Ticket> heldTickets = bookingService.holdTickets(request);
//
//                List<TicketDTO> response = heldTickets.stream().map(ticket -> {
//                    TicketDTO dto = new TicketDTO();
//                    dto.setSeatCode(ticket.getSeat().getSeatRow() + ticket.getSeat().getSeatNumber());
//                    dto.setPrice(ticket.getPrice());
//                    return dto;
//                }).toList();
//
//                return ResponseEntity.ok(Map.of(
//                        "tickets", response,
//                        "heldUntil", heldTickets.get(0).getHeldUntil(),
//                        "message", "Tickets held successfully"
//                ));
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage()
//                ));
//            }
//        }
//
//        /**
//         * API hủy ghế được giữ tạm thời
//         */
//        @DeleteMapping("/hold")
//        public ResponseEntity<?> cancelHeldTickets(@RequestBody HoldTicketRequest request) {
//            try {
//                if (request.getShowtimeId() == null || request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
//                    return ResponseEntity.badRequest().body("Missing required fields: showtimeId, seatIds");
//                }
//
//                bookingService.cancelHeldTickets(request);
//                return ResponseEntity.ok(Map.of(
//                        "message", "Held tickets cancelled successfully",
//                        "releasedSeats", request.getSeatIds().size()
//                ));
//            } catch (RuntimeException e) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", e.getMessage()
//                ));
//            }
//        }
//
//        /**
//         * Helper method để build BookingResponseDTO
//         */
//        private BookingResponseDTO buildBookingResponseDTO(Booking booking) {
//            BookingResponseDTO responseDTO = new BookingResponseDTO();
//            responseDTO.setId(booking.getId());
//            responseDTO.setStatus(booking.getStatus());
//            responseDTO.setBookingTime(booking.getBookingTime());
//            responseDTO.setTotalAmount(booking.getTotalAmount());
//            if (!booking.getTickets().isEmpty()) {
//                Showtime showtime = booking.getTickets().get(0).getShowtime();
//                if (showtime != null && showtime.getRoom() != null) {
//                    responseDTO.setRoom(convertRoomToDTO(showtime.getRoom()));
//                }
//            }
//            if (!booking.getTickets().isEmpty()) {
//                responseDTO.setShowtimeId((long) booking.getTickets().get(0).getShowtime().getId());
//
//            }
//            // Map tickets
//            List<TicketDTO> ticketDTOs = booking.getTickets().stream().map(ticket -> {
//                TicketDTO dto = new TicketDTO();
//                dto.setSeatId(ticket.getSeat().getId());
//                dto.setSeatCode(ticket.getSeat().getSeatRow() + ticket.getSeat().getSeatNumber());
//                dto.setPrice(ticket.getPrice());
//                return dto;
//            }).toList();
//            responseDTO.setTickets(ticketDTOs);
//
//            // Map combos (với null check)
//            if (booking.getCombos() != null && !booking.getCombos().isEmpty()) {
//                List<ComboOrderResponseDTO> comboDTOs = booking.getCombos().stream().map(combo -> {
//                    ComboOrderResponseDTO dto = new ComboOrderResponseDTO();
//                    dto.setName(combo.getCombo().getName());
//                    dto.setPrice(combo.getCombo().getPrice());
//                    dto.setQuantity(combo.getQuantity());
//                    return dto;
//                }).toList();
//                responseDTO.setCombos(comboDTOs);
//            }
//
//            return responseDTO;
//        }
//        public RoomDTO convertRoomToDTO(Room room) {
//            RoomDTO dto = new RoomDTO();
//            dto.setId(room.getId());
//            dto.setRoomNumber(String.valueOf(room.getRoomNumber()));
//            dto.setType(room.getType().getName());
//            return dto;
//        }
//    }