package com.example.cinema_booking.utils;

public class MaskingUtil {

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email; // Return unchanged if invalid or null
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = "@" + parts[1];
        String[] localParts = localPart.split("\\.");
        StringBuilder masked = new StringBuilder();
        for (String part : localParts) {
            if (part.length() > 0) {
                masked.append("*".repeat(part.length())).append(".");
            }
        }
        // Remove the last dot and append the domain
        if (masked.length() > 0) {
            masked.setLength(masked.length() - 1);
        }
        return masked.toString() + domain;
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return phoneNumber; // Return unchanged if too short or null
        }
        int visibleDigits = 4;
        int length = phoneNumber.length();
        String lastFour = phoneNumber.substring(length - visibleDigits);
        String maskedPart = "*".repeat(length - visibleDigits);
        return maskedPart + lastFour;
    }
}
