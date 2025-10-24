package com.example.cinema_booking.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtHelper {
    @Value("${jwt.key}")
    private String keyJWT;

    public boolean decryptToken(String token) {
        boolean result = false;
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyJWT));
        System.out.println(key);
        try {
            Jwts.parser().verifyWith(key).build().parseClaimsJws(token);
            result = true;
        }catch (Exception e) {
            System.out.println("Decrypt token error : " + e.getMessage());
        }


        return result;
    }

    public String getDataToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyJWT));
        String role = "";
        try {
            role = Jwts.parser().verifyWith(key).build().parseClaimsJws(token).getPayload().getSubject();

        }catch (Exception e) {
            System.out.println("Decrypt token error : " + e.getMessage());
        }
        return role;
    }

    public Integer getAccountIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyJWT));
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseClaimsJws(token)
                    .getPayload()
                    .get("accountId", Integer.class);
        } catch (Exception e) {
            System.out.println("Get accountId from token error: " + e.getMessage());
            return null;
        }
    }
}
