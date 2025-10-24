package com.example.cinema_booking.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TokenBlacklistService {
    
    // Sử dụng ConcurrentHashMap để thread-safe
    private final ConcurrentHashMap<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();
    
    /**
     * Thêm token vào blacklist
     * @param token JWT token cần blacklist
     */
    public void blacklistToken(String token) {
        blacklistedTokens.put(token, LocalDateTime.now());
    }
    
    /**
     * Kiểm tra token có trong blacklist không
     * @param token JWT token cần kiểm tra
     * @return true nếu token trong blacklist, false nếu không
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    /**
     * Xóa token khỏi blacklist (có thể dùng để unblacklist nếu cần)
     * @param token JWT token cần xóa khỏi blacklist
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
    
    /**
     * Lấy số lượng token trong blacklist
     * @return số lượng token
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
    
    /**
     * Xóa các token cũ khỏi blacklist (có thể gọi định kỳ để dọn dẹp)
     * @param hoursOld số giờ cũ để xóa
     */
    public void cleanupOldTokens(int hoursOld) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hoursOld);
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));
    }
    
    /**
     * Lấy tất cả token trong blacklist (chỉ dùng cho debug/admin)
     * @return Map chứa token và thời gian blacklist
     */
    public Map<String, LocalDateTime> getAllBlacklistedTokens() {
        return new ConcurrentHashMap<>(blacklistedTokens);
    }
} 