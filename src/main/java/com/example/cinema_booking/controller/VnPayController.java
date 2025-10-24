package com.example.cinema_booking.controller;

import com.example.cinema_booking.service.VnPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/vnpay")
public class VnPayController {

    private final VnPayService vnPayService;

    public VnPayController(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> create(@RequestParam long amount) throws Exception {
        String url = vnPayService.createPaymentUrl(amount);
        return ResponseEntity.ok(Collections.singletonMap("payUrl", url));
    }
}
