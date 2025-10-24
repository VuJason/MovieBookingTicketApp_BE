package com.example.cinema_booking.config;

import com.example.cinema_booking.service.AccountService;
import com.example.cinema_booking.utils.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomSecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;
    
    @Autowired
    private AccountService accountService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authenHeader = request.getHeader("Authorization");
        if (authenHeader != null && authenHeader.startsWith("Bearer ")) {
            String token = authenHeader.substring(7);
            
            // Kiểm tra token có bị blacklist không
            if (!accountService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            boolean isSuccess = jwtHelper.decryptToken(token);
            if (isSuccess) {
                String role = jwtHelper.getDataToken(token);
                Integer accountId = jwtHelper.getAccountIdFromToken(token);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                authorities.add(authority);

                SecurityContext securityContext = SecurityContextHolder.getContext();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    accountId, // Lưu accountId vào principal
                    "",
                    authorities
                );
                securityContext.setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
