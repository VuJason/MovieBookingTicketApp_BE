//package com.example.cinema_booking.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import vn.payos.PayOS;
//
//@Configuration
//public class PayOsConfig {
//
//    @Value("${payos.client-id}")
//    private String clientId;
//
//    @Value("${payos.api-key}")
//    private String apiKey;
//
//    @Value("${payos.check-sum-key}")
//    private String checksumKey;
//
//    @Bean
//    public PayOS payOS() {
//        return new PayOS(clientId, apiKey, checksumKey);
//    }
//}