package com.example.cinema_booking.service;

import com.example.cinema_booking.config.ZaloPayConfig;
import com.example.cinema_booking.dto.response.ZaloPayOrderResponse;
import com.example.cinema_booking.model.Booking;
import com.example.cinema_booking.utils.ZaloPayUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZaloPayService {
    
    private final ZaloPayConfig zaloPayConfig;
    private final Gson gson = new Gson();
    
    /**
     * Tạo RestTemplate với timeout config
     */
    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30 seconds
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }
    
    /**
     * Tạo order ZaloPay cho booking
     */
    public ZaloPayOrderResponse createOrder(Booking booking) throws Exception {
        // Tạo app_trans_id unique
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String appTransId = sdf.format(new Date()) + "_" + booking.getId();
        
        // Tạo embed_data cho mobile deep link
        Map<String, Object> embedData = new HashMap<>();
        embedData.put("redirecturl", "moviebooking://payment/callback");
        embedData.put("bookingId", booking.getId());
        
        // Tạo item data
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("itemid", "booking_" + booking.getId());
        item.put("itemname", "Đặt vé xem phim");
        item.put("itemprice", booking.getTotalAmount().longValue());
        item.put("itemquantity", 1);
        items.add(item);
        
        // Tạo order data
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("app_id", Integer.parseInt(zaloPayConfig.getAppId()));
        orderData.put("app_trans_id", appTransId);
        orderData.put("app_user", "user_" + booking.getUser().getId());
        orderData.put("app_time", System.currentTimeMillis());
        orderData.put("amount", booking.getTotalAmount().longValue());
        orderData.put("description", "Thanh toán đặt vé #" + booking.getId());
        orderData.put("bank_code", "");
        orderData.put("item", gson.toJson(items));
        orderData.put("embed_data", gson.toJson(embedData));
        orderData.put("callback_url", zaloPayConfig.getCallbackUrl());
        
        // Tạo MAC
        String data = orderData.get("app_id") + "|" 
                    + orderData.get("app_trans_id") + "|" 
                    + orderData.get("app_user") + "|" 
                    + orderData.get("amount") + "|" 
                    + orderData.get("app_time") + "|" 
                    + orderData.get("embed_data") + "|" 
                    + orderData.get("item");
        
        String mac = ZaloPayUtils.generateHMAC(data, zaloPayConfig.getKey1());
        orderData.put("mac", mac);
        
        // Gửi request tới ZaloPay với RestTemplate
        try {
            RestTemplate restTemplate = createRestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> request = new HttpEntity<>(
                gson.toJson(orderData), 
                headers
            );
            
            log.info("Sending request to ZaloPay: {}", zaloPayConfig.getEndpoint());
            log.info("Request data: app_trans_id={}, amount={}", appTransId, booking.getTotalAmount());
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                zaloPayConfig.getEndpoint(),
                request,
                String.class
            );
            
            log.info("ZaloPay response status: {}", response.getStatusCode());
            log.info("ZaloPay response body: {}", response.getBody());
            
            ZaloPayOrderResponse orderResponse = gson.fromJson(
                response.getBody(), 
                ZaloPayOrderResponse.class
            );
            
            if (orderResponse.getReturnCode() != 1) {
                throw new RuntimeException("ZaloPay order creation failed: " + orderResponse.getReturnMessage());
            }
            
            return orderResponse;
            
        } catch (Exception e) {
            log.error("Error calling ZaloPay API: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể kết nối đến ZaloPay. Vui lòng kiểm tra kết nối mạng hoặc thử lại sau: " + e.getMessage());
        }
    }
    
    /**
     * Verify callback từ ZaloPay
     */
    public boolean verifyCallback(Map<String, Object> callbackData) {
        try {
            String dataStr = callbackData.get("data").toString();
            String reqMac = callbackData.get("mac").toString();
            
            String mac = ZaloPayUtils.generateHMAC(dataStr, zaloPayConfig.getKey2());
            
            return mac.equals(reqMac);
        } catch (Exception e) {
            log.error("Error verifying ZaloPay callback", e);
            return false;
        }
    }
}
