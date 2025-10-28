package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.MovieDTO;
import com.example.cinema_booking.dto.RoomDTO;
import com.example.cinema_booking.dto.TicketDTO;
import com.example.cinema_booking.dto.request.BookingRequestDTO;
import com.example.cinema_booking.dto.request.HoldTicketRequest;
import com.example.cinema_booking.dto.response.BookingResponseDTO;
import com.example.cinema_booking.dto.response.ComboOrderResponseDTO;
import com.example.cinema_booking.model.*;

import com.example.cinema_booking.dto.response.ZaloPayOrderResponse;
import com.example.cinema_booking.service.BookingService;
import com.example.cinema_booking.service.ZaloPayService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ZaloPayService zaloPayService;

        /**
         * API tạo booking - CHỈ tạo booking, KHÔNG tạo PayOS link
         */
        @PostMapping
        public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO requestDTO) {
            try {
                // Validate input
                if (
                        requestDTO.getShowtimeId() <= 0 ||
                        requestDTO.getSeatIds() == null ||
                        requestDTO.getSeatIds().isEmpty()) {

                    return ResponseEntity.badRequest().body("Missing required fields: userId, showtimeId, seatIds");
                }

                // Tạo booking
                Booking booking = bookingService.createBooking(requestDTO);

                // Trả về thông tin booking
                BookingResponseDTO responseDTO = buildBookingResponseDTO(booking);
                return ResponseEntity.ok(responseDTO);

            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of(
                        "error", "Internal server error: " + e.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
            }
        }

        /**
         * API tạo link thanh toán PayOS cho booking đã tồn tại
         */
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

        /**
         * API tạo link thanh toán ZaloPay cho booking (dành cho mobile app)
         */
        @PostMapping("/{bookingId}/zalopay-payment")
        public ResponseEntity<?> createZaloPayPayment(@PathVariable Long bookingId) {
            try {
                // Validate booking
                if (!bookingService.isBookingValidForPayment(bookingId)) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Booking không hợp lệ hoặc đã hết hạn thanh toán",
                            "bookingId", bookingId
                    ));
                }

                Booking booking = bookingService.getBookingById(bookingId);

                // Tạo ZaloPay order
                ZaloPayOrderResponse zaloPayResponse = zaloPayService.createOrder(booking);

                // Lưu payment URL vào booking
                booking.setPaymentUrl(zaloPayResponse.getOrderUrl());
                bookingService.updateBooking(booking);

                return ResponseEntity.ok(Map.of(
                        "returnCode", zaloPayResponse.getReturnCode(),
                        "returnMessage", zaloPayResponse.getReturnMessage(),
                        "orderUrl", zaloPayResponse.getOrderUrl(),
                        "zpTransToken", zaloPayResponse.getZpTransToken(),
                        "orderToken", zaloPayResponse.getOrderToken(),
                        "bookingId", booking.getId(),
                        "amount", booking.getTotalAmount(),
                        "status", "ZALOPAY_ORDER_CREATED",
                        "expiresAt", booking.getBookingTime().plusMinutes(15)
                ));

            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage(),
                        "bookingId", bookingId
                ));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of(
                        "error", "Lỗi tạo order ZaloPay: " + e.getMessage(),
                        "bookingId", bookingId
                ));
            }
        }

        /**
         * API webhook xử lý callback từ ZaloPay
         */
        @PostMapping("/webhook/zalopay")
        public ResponseEntity<?> handleZaloPayCallback(@RequestBody Map<String, Object> callbackData) {
            try {
                System.out.println("ZaloPay Callback received: " + callbackData);

                // Verify MAC
                if (!zaloPayService.verifyCallback(callbackData)) {
                    System.err.println("ZaloPay callback MAC verification failed");
                    return ResponseEntity.ok(Map.of(
                            "return_code", -1,
                            "return_message", "MAC verification failed"
                    ));
                }

                // Parse data
                String dataStr = callbackData.get("data").toString();
                com.google.gson.Gson gson = new com.google.gson.Gson();
                Map<String, Object> data = gson.fromJson(dataStr, Map.class);

                String appTransId = data.get("app_trans_id").toString();
                // Extract bookingId từ app_trans_id (format: yyMMdd_bookingId)
                Long bookingId = Long.valueOf(appTransId.split("_")[1]);

                System.out.println("ZaloPay Callback - BookingId: " + bookingId);

                // Xác nhận payment + gửi mail
                bookingService.confirmBookingPayment(bookingId);
                System.out.println("Booking " + bookingId + " payment confirmed via ZaloPay");


                return ResponseEntity.ok(Map.of(
                        "return_code", 1,
                        "return_message", "success"
                ));

            } catch (Exception e) {
                System.err.println("ZaloPay Callback processing failed: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.ok(Map.of(
                        "return_code", 0,
                        "return_message", "Processing failed: " + e.getMessage()
                ));
            }
        }

        /**
         * API webhook xử lý kết quả thanh toán từ PayOS
         */
        @PostMapping("/webhook/payos")
        public ResponseEntity<?> handlePayOSWebhook(@RequestBody Map<String, Object> webhookData) {
            try {
                String status = (String) webhookData.get("status");
                Long bookingId = Long.valueOf(webhookData.get("orderCode").toString());

                System.out.println("PayOS Webhook - BookingId: " + bookingId + ", Status: " + status);

                if ("PAID".equals(status) || "SUCCESS".equals(status)) {
                    // ✅ Xác nhận payment + gửi mail
                    bookingService.confirmBookingPayment(bookingId);
                    System.out.println("Booking " + bookingId + " payment confirmed & email sent successfully via webhook");
                } else if ("CANCELLED".equals(status) || "FAILED".equals(status)) {
                    bookingService.cancelBooking(bookingId);
                    System.out.println("Booking " + bookingId + " cancelled due to payment " + status);
                }

                return ResponseEntity.ok("OK");
            } catch (Exception e) {
                System.err.println("PayOS Webhook processing failed: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Webhook processing failed");
            }
        }
        /**
         * API xác nhận thanh toán thành công (Manual - backup cho webhook)
         */
        @PutMapping("/{bookingId}/confirm")
        public ResponseEntity<?> confirmBookingPayment(@PathVariable Long bookingId) {
            try {
                Booking booking = bookingService.confirmBookingPayment(bookingId);
                return ResponseEntity.ok(Map.of(
                        "message", "Đã xác nhận thanh toán thành công",
                        "bookingId", booking.getId(),
                        "status", booking.getStatus(),
                        "confirmedAt", LocalDateTime.now()
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage(),
                        "bookingId", bookingId
                ));
            }
        }

        /**
         * API hủy booking
         */
        @PutMapping("/{bookingId}/cancel")
        public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
            try {
                Booking booking = bookingService.cancelBooking(bookingId);
                return ResponseEntity.ok(Map.of(
                        "message", "Đã hủy booking và giải phóng ghế",
                        "bookingId", booking.getId(),
                        "status", booking.getStatus(),
                        "cancelledAt", LocalDateTime.now()
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage(),
                        "bookingId", bookingId
                ));
            }
        }

        /**
         * API lấy thông tin booking
         */
        @GetMapping("/{bookingId}")
        public ResponseEntity<?> getBooking(@PathVariable Long bookingId) {
            try {
                Booking booking = bookingService.getBookingById(bookingId);
                BookingResponseDTO responseDTO = buildBookingResponseDTO(booking);
                return ResponseEntity.ok(responseDTO);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage(),
                        "bookingId", bookingId
                ));
            }
        }

        /**
         * API lấy booking SUCCESS của user hiện tại (từ token)
         * Chỉ trả về những booking đã thanh toán thành công
         */
        @GetMapping("/my-bookings")
        public ResponseEntity<?> getMyBookings() {
            try {
                List<Booking> bookings = bookingService.getMyBookings();
                List<BookingResponseDTO> responseDTOs = bookings.stream()
                        .map(this::buildBookingResponseDTO)
                        .toList();
                return ResponseEntity.ok(responseDTOs);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage()
                ));
            }
        }

        /**
         * API lấy booking của user khác (Admin only - optional)
         */
        @GetMapping("/user/{userId}")
        public ResponseEntity<?> getUserBookings(@PathVariable Long userId) {
            try {
                List<Booking> bookings = bookingService.getBookingsByUserId(userId);
                List<BookingResponseDTO> responseDTOs = bookings.stream()
                        .map(this::buildBookingResponseDTO)
                        .toList();
                return ResponseEntity.ok(responseDTOs);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage(),
                        "userId", userId
                ));
            }
        }

        /**
         * API giữ ghế tạm thời (không tạo booking)
         */
        @PostMapping("/hold")
        public ResponseEntity<?> holdTickets(@RequestBody HoldTicketRequest request) {
            try {
                if (request.getShowtimeId() == 0 || request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
                    return ResponseEntity.badRequest().body("Missing required fields: showtimeId, seatIds");
                }

                List<Ticket> heldTickets = bookingService.holdTickets(request);

                List<TicketDTO> response = heldTickets.stream().map(ticket -> {
                    TicketDTO dto = new TicketDTO();
                    dto.setSeatId(ticket.getSeat().getId());
                    dto.setSeatCode(ticket.getSeat().getSeatRow() + ticket.getSeat().getSeatNumber());
                    dto.setPrice(ticket.getPrice());
                    return dto;
                }).toList();

                return ResponseEntity.ok(Map.of(
                        "tickets", response,
                        "heldUntil", heldTickets.get(0).getHeldUntil(),
                        "message", "Tickets held successfully"
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage()
                ));
            }
        }

        /**
         * API hủy ghế được giữ tạm thời
         */
        @DeleteMapping("/hold")
        public ResponseEntity<?> cancelHeldTickets(@RequestBody HoldTicketRequest request) {
            try {
                if (request.getShowtimeId() == 0 || request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
                    return ResponseEntity.badRequest().body("Missing required fields: showtimeId, seatIds");
                }

                bookingService.cancelHeldTickets(request);
                return ResponseEntity.ok(Map.of(
                        "message", "Held tickets cancelled successfully",
                        "releasedSeats", request.getSeatIds().size()
                ));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", e.getMessage()
                ));
            }
        }

        /**
         * Helper method để build BookingResponseDTO
         */
        private BookingResponseDTO buildBookingResponseDTO(Booking booking) {
            BookingResponseDTO responseDTO = new BookingResponseDTO();
            responseDTO.setId(booking.getId());
            responseDTO.setStatus(booking.getStatus());
            responseDTO.setBookingTime(booking.getBookingTime());
            responseDTO.setTotalAmount(booking.getTotalAmount());
            responseDTO.setPaymentUrl(booking.getPaymentUrl());
            
            // Map showtime (lấy từ ticket đầu tiên)
            if (!booking.getTickets().isEmpty()) {
                Showtime showtime = booking.getTickets().get(0).getShowtime();
                if (showtime != null) {
                    responseDTO.setShowtime(convertShowtimeToDTO(showtime));
                }
            }
            
            // Map tickets
            List<TicketDTO> ticketDTOs = booking.getTickets().stream().map(ticket -> {
                TicketDTO dto = new TicketDTO();
                dto.setSeatId(ticket.getSeat().getId());
                dto.setSeatCode(ticket.getSeat().getSeatRow() + ticket.getSeat().getSeatNumber());
                dto.setPrice(ticket.getPrice());
                return dto;
            }).toList();
            responseDTO.setTickets(ticketDTOs);

            // Map combos (với null check)
            if (booking.getCombos() != null && !booking.getCombos().isEmpty()) {
                List<ComboOrderResponseDTO> comboDTOs = booking.getCombos().stream().map(combo -> {
                    ComboOrderResponseDTO dto = new ComboOrderResponseDTO();
                    dto.setName(combo.getCombo().getName());
                    dto.setPrice(combo.getCombo().getPrice());
                    dto.setQuantity(combo.getQuantity());
                    return dto;
                }).toList();
                responseDTO.setCombos(comboDTOs);
            }

            return responseDTO;
        }
        
        /**
         * Convert Showtime entity to ShowtimeDTO
         */
        private com.example.cinema_booking.dto.ShowtimeDTO convertShowtimeToDTO(Showtime showtime) {
            com.example.cinema_booking.dto.ShowtimeDTO dto = new com.example.cinema_booking.dto.ShowtimeDTO();
            dto.setId(showtime.getId());
            dto.setStartTime(showtime.getStartTime());
            dto.setEndTime(showtime.getEndTime());
            dto.setStatus(showtime.getStatus());
            
            // Map movie
            if (showtime.getMovie() != null) {
                dto.setMovie(convertMovieToDTO(showtime.getMovie()));
            }
            
            // Map room
            if (showtime.getRoom() != null) {
                dto.setRoom(convertRoomToDTO(showtime.getRoom()));
            }
            
            return dto;
        }
        
        /**
         * Convert Movie entity to MovieDTO
         */
        private MovieDTO convertMovieToDTO(Movie movie) {
            com.example.cinema_booking.dto.MovieDTO dto = new com.example.cinema_booking.dto.MovieDTO();
            dto.setId(movie.getId());
            dto.setName(movie.getName());
            dto.setDescription(movie.getDescription());
            dto.setDuration(movie.getDuration());
            dto.setDirector(movie.getDirector());
            dto.setActor(movie.getActor());
            dto.setReleaseDate(movie.getReleaseDate());
            dto.setTrailer(movie.getTrailer());
            dto.setPosterUrl(movie.getPosterUrl());
            return dto;
        }
        public RoomDTO convertRoomToDTO(Room room) {
            RoomDTO dto = new RoomDTO();
            dto.setId(room.getId());
            dto.setRoomNumber(String.valueOf(room.getRoomNumber()));
            dto.setType(room.getType().getName());
            return dto;
        }
    }