package com.example.cinema_booking.controller;

import com.example.cinema_booking.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/html")
    public ResponseEntity<String> sendTestHtmlEmail(@RequestParam String to) {
        try {
            String subject = "🎟️ Test Gửi Email HTML";
            String content = """
                    <html>
                    <body style='font-family:Arial,sans-serif'>
                        <h2 style='color:purple'>✅ Test gửi Email HTML thành công!</h2>
                        <p><b>Mã đặt:</b> TEST-123456</p>
                        <p><b>Ghế:</b> A1, A2</p>
                        <p><b>Combo:</b> Bắp + Pepsi</p>
                        <p><b>Tổng:</b> <span style='color:green'>180,000 VND</span></p>
                        <br>
                        <p>Cảm ơn bạn đã sử dụng hệ thống!</p>
                    </body>
                    </html>
                    """;

            emailService.sendHtmlEmail(to, subject, content);
            return ResponseEntity.ok("✅ Email HTML đã được gửi đến " + to);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Gửi email thất bại: " + e.getMessage());
        }
    }
}
