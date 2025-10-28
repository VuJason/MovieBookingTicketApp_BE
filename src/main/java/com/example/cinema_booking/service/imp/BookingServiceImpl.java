package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.dto.ComboOrderDTO;
import com.example.cinema_booking.dto.RoomDTO;
import com.example.cinema_booking.dto.TicketDTO;
import com.example.cinema_booking.dto.request.BookingRequestDTO;
import com.example.cinema_booking.dto.request.HoldTicketRequest;
import com.example.cinema_booking.model.*;
import com.example.cinema_booking.repository.*;
import com.example.cinema_booking.service.BookingService;
import com.example.cinema_booking.service.EmailService;
import com.example.cinema_booking.utils.EmailContentBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final AccountRepository accountRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final BookingComboRepository bookingComboRepository;
    private final ComboRepository comboRepository;
    private final EmailService emailService;

    /**
     * Tạo booking mới - CHỈ tạo booking, KHÔNG tạo PayOS link
     */
    public Booking createBooking(BookingRequestDTO dto) {
        // Lấy accountId từ Security Context (fix ClassCastException)
        int accountId = com.example.cinema_booking.utils.AuthenticationUtils.getCurrentAccountId();
        
        // Lấy user
        Account user = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy suất chiếu
        Showtime showtime = showtimeRepository.findById(dto.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // Lấy danh sách ghế
        List<Seat> selectedSeats = seatRepository.findAllById(dto.getSeatIds());
        if (selectedSeats.size() != dto.getSeatIds().size()) {
            throw new RuntimeException("Some seats not found");
        }

        List<Ticket> heldTickets = ticketRepository.findBySeatInAndShowtimeAndStatusAndBookingIsNull(
                selectedSeats, showtime, "HELD"
        );

        // Kiểm tra xem ghế có bị đặt hoặc giữ bởi người khác không
        if (heldTickets.size() != selectedSeats.size()) {
            throw new RuntimeException("Một số ghế chưa được giữ hợp lệ hoặc đã bị người khác giữ/đặt");
        }

        // Tạo Booking với trạng thái PENDING
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("PENDING"); // Booking ở trạng thái chờ thanh toán

        double total = 0;

        // Tạo tickets với trạng thái HELD (chưa BOOKED)
        List<Ticket> tickets = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setShowtime(showtime);
            ticket.setBooking(booking);
            ticket.setStatus("HELD"); // Ghế chỉ được giữ, chưa BOOKED
            ticket.setHeldUntil(LocalDateTime.now().plusMinutes(15)); // Giữ ghế 15 phút để thanh toán
            ticket.setPrice(getTicketPrice(seat)); // Dynamic pricing theo loại ghế

            total += ticket.getPrice();
            tickets.add(ticket);
        }

        booking.setTickets(tickets);

        // Xử lý combo nếu có
        List<BookingCombo> bookingCombos = new ArrayList<>();
        if (dto.getCombos() != null && !dto.getCombos().isEmpty()) {
            for (ComboOrderDTO comboDto : dto.getCombos()) {
                Combo combo = comboRepository.findById(comboDto.getComboId())
                        .orElseThrow(() -> new RuntimeException("Combo with ID " + comboDto.getComboId() + " not found"));

                if (comboDto.getQuantity() <= 0) {
                    throw new RuntimeException("Combo quantity must be greater than 0");
                }

                BookingCombo bookingCombo = new BookingCombo();
                bookingCombo.setBooking(booking);
                bookingCombo.setCombo(combo);
                bookingCombo.setQuantity(comboDto.getQuantity());

                total += combo.getPrice() * comboDto.getQuantity();
                bookingCombos.add(bookingCombo);
            }
            booking.setCombos(bookingCombos);
        }

        booking.setTotalAmount(total);

        // Lưu booking
        Booking savedBooking = bookingRepository.save(booking);

        System.out.println("Created booking ID: " + savedBooking.getId() + " with status: " + savedBooking.getStatus());
        return savedBooking;
    }

    /**
     * Giữ ghế tạm thời (không có booking)
     */
    public List<Ticket> holdTickets(HoldTicketRequest request) {
        // Lấy suất chiếu
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // Lấy danh sách ghế
        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        // Kiểm tra đủ số lượng ghế
        if (seats.size() != request.getSeatIds().size()) {
            throw new RuntimeException("Một số ghế không tồn tại");
        }

        List<Ticket> heldTickets = new ArrayList<>();

        for (Seat seat : seats) {
            // Kiểm tra kỹ xem ghế này đã được giữ hoặc đặt chưa
            List<Ticket> existing = ticketRepository.findBySeatAndShowtimeAndStatusIn(
                    seat, showtime, List.of("HELD", "BOOKED")
            );

            if (!existing.isEmpty()) {
                throw new RuntimeException("Seat " + seat.getSeatRow() + seat.getSeatNumber() + " is already held or booked.");
            }

            // Tạo ticket HELD tạm thời
            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setShowtime(showtime);
            ticket.setStatus("HELD");
            ticket.setHeldUntil(LocalDateTime.now().plusMinutes(10));
            ticket.setPrice(getTicketPrice(seat));

            heldTickets.add(ticket);
        }

        // Lưu tất cả
        return ticketRepository.saveAll(heldTickets);
    }

    /**
     * Hủy ghế được giữ tạm thời
     */
    public void cancelHeldTickets(HoldTicketRequest request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        // Chỉ xóa tickets HELD không có booking (held tạm thời)
        List<Ticket> heldTickets = ticketRepository.findBySeatInAndShowtimeAndStatusAndBookingIsNull(
                seats, showtime, "HELD"
        );

        ticketRepository.deleteAll(heldTickets);
        System.out.println("Released " + heldTickets.size() + " held tickets");
    }

    /**
     * Hủy booking và giải phóng ghế
     */
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        if ("SUCCESS".equals(booking.getStatus())) {
            throw new RuntimeException("Không thể hủy booking đã thanh toán thành công");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking đã được hủy rồi");
        }

        // Cập nhật trạng thái
        booking.setStatus("CANCELLED");

        // Xóa các ticket liên quan
        List<Ticket> tickets = new ArrayList<>(booking.getTickets()); // clone để tránh dùng lại entity đã xoá
        ticketRepository.deleteAll(tickets);

        booking.getTickets().clear(); // dọn lại danh sách tránh Hibernate merge ticket đã xoá

        Booking savedBooking = bookingRepository.save(booking);

        System.out.println("Cancelled booking ID: " + bookingId + " and released " + tickets.size() + " seats");
        return savedBooking;
    }


    /**
     * Xác nhận thanh toán thành công - chuyển ghế từ HELD sang BOOKED
     */
    public Booking confirmBookingPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        if ("SUCCESS".equals(booking.getStatus())) {
            throw new RuntimeException("Booking đã được thanh toán rồi");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Không thể xác nhận booking đã bị hủy");
        }

        // Cập nhật trạng thái booking
        booking.setStatus("SUCCESS");
        // Chuyển tất cả tickets từ HELD sang BOOKED
        for (Ticket ticket : booking.getTickets()) {
            ticket.setStatus("BOOKED");
            ticket.setHeldUntil(null); // Không cần held time nữa
        }

        Booking savedBooking = bookingRepository.save(booking);

        List<Ticket> expiredHeldTickets = ticketRepository.findByStatusAndBookingIsNull(
                "HELD"
        );
        if (!expiredHeldTickets.isEmpty()) {
            ticketRepository.deleteAll(expiredHeldTickets);
            System.out.println("Auto-released " + expiredHeldTickets.size() + " expired held tickets");
        }



        // Gửi email xác nhận sau khi thanh toán thành công
        try {
            String html = EmailContentBuilder.buildBookingEmail(savedBooking);
            emailService.sendHtmlEmail(booking.getUser().getEmail(), "Xác nhận đặt vé thành công", html);
            System.out.println("Sent confirmation email to: " + booking.getUser().getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
            // Không throw exception để không ảnh hưởng đến việc confirm booking
        }

        System.out.println("Confirmed payment for booking ID: " + bookingId);
        return savedBooking;
    }

    /**
     * Kiểm tra booking có hợp lệ để thanh toán không
     */
    public boolean isBookingValidForPayment(Long bookingId) {
        try {
            Booking booking = getBookingById(bookingId);

            // Kiểm tra status
            if (!"PENDING".equals(booking.getStatus())) {
                return false;
            }

            // Kiểm tra thời gian hết hạn (10 phút)
            if (booking.getBookingTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Scheduled(fixedRate = 60000) // chạy mỗi 60 giây
    public void releaseExpiredHeldTickets() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Xóa các tickets HELD đã hết hạn (không có booking - hold tạm thời)
        List<Ticket> expiredHeldTickets = ticketRepository.findByStatusAndHeldUntilBeforeAndBookingIsNull(
                "HELD", now
        );
        if (!expiredHeldTickets.isEmpty()) {
            ticketRepository.deleteAll(expiredHeldTickets);
            System.out.println("Auto-released " + expiredHeldTickets.size() + " expired held tickets");
        }

        // 2. Hủy các booking PENDING đã hết hạn (15 phút)
        List<Booking> expiredBookings = bookingRepository.findByStatusAndBookingTimeBefore(
                "PENDING", now.minusMinutes(10)
        );

        for (Booking booking : expiredBookings) {
            try {
                cancelBooking(booking.getId());
                System.out.println("Auto-cancelled expired booking: " + booking.getId());
            } catch (Exception e) {
                System.err.println("Error cancelling expired booking " + booking.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Lấy thông tin booking theo ID
     */
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));
    }

    /**
     * Lấy tất cả booking của user
     */
    public List<Booking> getBookingsByUserId(Long userId) {
        Account user = accountRepository.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserOrderByBookingTimeDesc(user);
    }

    /**
     * Lấy tất cả booking SUCCESS của user hiện tại (từ Security Context)
     */
    public List<Booking> getMyBookings() {
        // Lấy accountId từ Security Context
        int accountId = com.example.cinema_booking.utils.AuthenticationUtils.getCurrentAccountId();

        Account user = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chỉ lấy bookings có status SUCCESS
        return bookingRepository.findByUserAndStatusOrderByBookingTimeDesc(user, "SUCCESS");
    }

    /**
     * Cập nhật booking
     */
    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    /**
     * Tính giá vé theo loại ghế (có thể mở rộng)
     */
    private double getTicketPrice(Seat seat) {
        return 70000.0;
    }
}