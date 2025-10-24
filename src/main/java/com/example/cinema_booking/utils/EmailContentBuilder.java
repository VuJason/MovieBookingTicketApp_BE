package com.example.cinema_booking.utils;

import com.example.cinema_booking.model.Booking;
import com.example.cinema_booking.model.BookingCombo;
import com.example.cinema_booking.model.Showtime;
import com.example.cinema_booking.model.Ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EmailContentBuilder {

    public static String buildBookingEmail(Booking booking) {
        try {
            // Đọc template HTML từ file
            String template = loadEmailTemplate();

            // Lấy thông tin showtime
            Showtime showtime = booking.getTickets().get(0).getShowtime();

            // Định dạng ngày giờ thành dd-MM-yyyy HH:mm
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedShowtime = showtime.getStartTime().format(formatter);

            // Thay thế các placeholder cơ bản
            template = template.replace("{{BOOKING_ID}}", String.valueOf(booking.getId()));
            template = template.replace("{{MOVIE_NAME}}", showtime.getMovie().getName());
            template = template.replace("{{SHOWTIME}}", formattedShowtime);
            template = template.replace("{{ROOM_NUMBER}}", String.valueOf(showtime.getRoom().getRoomNumber()));

            // Tạo danh sách items
            StringBuilder orderItems = new StringBuilder();
            int stt = 1;

            // Thêm vé
            for (Ticket t : booking.getTickets()) {
                orderItems.append(createOrderRow(
                        stt++,
                        "Ghế " + t.getSeat().getSeatRow() + t.getSeat().getSeatNumber(),
                        1,
                        t.getPrice(),
                        t.getPrice(),
                        false
                ));
            }

            // Thêm combo
            for (BookingCombo bc : booking.getCombos()) {
                double subtotal = bc.getQuantity() * bc.getCombo().getPrice();
                orderItems.append(createOrderRow(
                        stt++,
                        bc.getCombo().getName(),
                        bc.getQuantity(),
                        bc.getCombo().getPrice(),
                        subtotal,
                        true
                ));
            }

            template = template.replace("{{ORDER_ITEMS}}", orderItems.toString());
            template = template.replace("{{TOTAL_AMOUNT}}", formatCurrency(booking.getTotalAmount()));

            return template;

        } catch (Exception e) {
            e.printStackTrace();
            return buildBookingEmailFallback(booking);
        }
    }

    private static String loadEmailTemplate() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/templates/email-template-v2.html")));
    }

    private static String createOrderRow(int stt, String itemName, int quantity, double unitPrice, double totalPrice, boolean isCombo) {
        String bgColor = stt % 2 == 0 ? "background-color: #f8f9fa;" : "background-color: #ffffff;";
        String itemIcon = isCombo ? "🍿 " : "🎫 ";

        return String.format(
                "<tr style='%s'>" +
                        "<td style='text-align: center; border: 1px solid #bdc3c7; color: #2c3e50;'>%d</td>" +
                        "<td style='border: 1px solid #bdc3c7; color: #2c3e50;'>%s%s</td>" +
                        "<td style='text-align: center; border: 1px solid #bdc3c7; color: #2c3e50;'>%d</td>" +
                        "<td style='text-align: right; border: 1px solid #bdc3c7; color: #2c3e50;'>%s</td>" +
                        "<td style='text-align: right; border: 1px solid #bdc3c7; color: #2c3e50; font-weight: bold;'>%s</td>" +
                        "</tr>",
                bgColor, stt, itemIcon, itemName, quantity,
                formatCurrency(unitPrice), formatCurrency(totalPrice)
        );
    }

    private static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(Math.round(amount));
    }

    private static String buildBookingEmailFallback(Booking booking) {
        // Fallback về code cũ nếu có lỗi
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Arial,sans-serif;'>");
        sb.append("<h2 style='color:#2c3e50'>XÁC NHẬN ĐẶT VÉ THÀNH CÔNG</h2>");
        sb.append("<p><b>MÃ VÉ:</b> BOOKING-").append(booking.getId()).append("</p>");
        // ... thêm code fallback đơn giản
        sb.append("</body></html>");
        return sb.toString();
    }
}
