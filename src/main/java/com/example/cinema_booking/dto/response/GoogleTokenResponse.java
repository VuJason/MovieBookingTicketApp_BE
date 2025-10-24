package com.example.cinema_booking.dto.response;

import lombok.Data;

@Data
public class GoogleTokenResponse {
    private String email;
    private String name;
    private String picture;
    private String token;
    private String locale;
    private String givenName;
    private String familyName;
}
