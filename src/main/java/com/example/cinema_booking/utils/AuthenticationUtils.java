package com.example.cinema_booking.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class để xử lý authentication và lấy user ID
 */
public class AuthenticationUtils {
    
    /**
     * Lấy account ID từ Security Context
     * @return account ID
     * @throws RuntimeException nếu không thể lấy account ID
     */
    public static int getCurrentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof Integer) {
            return (Integer) principal;
        } else if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user authentication: principal is not a valid user ID");
            }
        } else {
            throw new RuntimeException("Invalid user authentication: unexpected principal type - " + principal.getClass().getName());
        }
    }
    
    /**
     * Kiểm tra xem user có đang authenticated không
     * @return true nếu authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
