package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.*;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.dto.response.GoogleTokenResponse;
import com.example.cinema_booking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.example.cinema_booking.dto.request.EmailVerificationRequest;

@RestController
@RequestMapping("/api")
public class AuthenController {

    @Autowired
    private AccountService accountService;



//    @GetMapping("/auth/signingoogle")
//    public Map<String,Object>  currentUser(@AuthenticationPrincipal OidcUser oidcUser) {
//        return oidcUser.getAttributes();
//    }


    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setCode(200);
            baseResponse.setMessage("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
            baseResponse.setData(accountService.registerWithEmailVerification(registerRequest));
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setCode(400);
            baseResponse.setMessage(e.getMessage());
            baseResponse.setData(null);
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PostMapping("/auth/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        
        try {
            boolean success = accountService.verifyEmail(request.getEmail(), request.getVerificationToken());
            
            if (success) {
                baseResponse.setCode(200);
                baseResponse.setMessage("Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.");
                baseResponse.setData(Map.of("verified", true, "email", request.getEmail()));
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage("Token xác thực không hợp lệ hoặc đã hết hạn");
                baseResponse.setData(Map.of("verified", false, "email", request.getEmail()));
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
        }
        
        return ResponseEntity.ok(baseResponse);
    }

    /**
     * API xử lý link xác thực từ email - chuyển hướng về login
     */
    @GetMapping("/auth/verify-email")
    public ResponseEntity<?> verifyEmailFromLink(@RequestParam String email, @RequestParam String token) {
        BaseResponse baseResponse = new BaseResponse();
        
        try {
            boolean success = accountService.verifyEmail(email, token);
            
            if (success) {
                baseResponse.setCode(200);
                baseResponse.setMessage("Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.");
                baseResponse.setData(Map.of(
                    "verified", true, 
                    "email", email,
                    "redirectUrl", "/login",
                    "message", "Tài khoản đã được xác thực thành công. Vui lòng đăng nhập."
                ));
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage("Link xác thực không hợp lệ hoặc đã hết hạn");
                baseResponse.setData(Map.of(
                    "verified", false, 
                    "email", email,
                    "redirectUrl", "/register",
                    "message", "Link xác thực không hợp lệ. Vui lòng đăng ký lại."
                ));
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
            baseResponse.setData(Map.of(
                "verified", false, 
                "email", email,
                "redirectUrl", "/register",
                "message", "Có lỗi xảy ra. Vui lòng thử lại."
            ));
        }
        
        return ResponseEntity.ok(baseResponse);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = accountService.login(loginRequest.getEmail(), loginRequest.getPassword());
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(200);
        baseResponse.setData(token);
        return ResponseEntity.ok(baseResponse);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@AuthenticationPrincipal OAuth2User principal) {
        BaseResponse response = new BaseResponse();
        if (principal != null) {
            GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
            tokenResponse.setEmail(principal.getAttribute("email"));
            tokenResponse.setName(principal.getAttribute("name"));
            tokenResponse.setPicture(principal.getAttribute("picture"));
            // Gọi service để lưu user và sinh JWT token
            String token = accountService.createOrUpdateGoogleUser(tokenResponse);
            tokenResponse.setToken(token);
            response.setCode(200);
            response.setMessage("Google authentication successful");
            response.setData(tokenResponse);
        } else {
            response.setCode(401);
            response.setMessage("Google authentication failed");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * API gửi OTP cho chức năng quên mật khẩu
     */
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        BaseResponse baseResponse = new BaseResponse();

        try {
            var result = accountService.sendForgotPasswordOtp(request.getEmail());

            if (result.isSuccess()) {
                baseResponse.setCode(200);
                baseResponse.setMessage(result.getMessage());
                baseResponse.setData(result);
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage(result.getMessage());
                baseResponse.setData(result);
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(baseResponse);
    }

    /**
     * API xác thực OTP
     */
    @PostMapping("/auth/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        BaseResponse baseResponse = new BaseResponse();

        try {
            boolean isValid = accountService.verifyOtp(request.getEmail(), request.getOtp());

            if (isValid) {
                baseResponse.setCode(200);
                baseResponse.setMessage("OTP hợp lệ");
                baseResponse.setData(Map.of("valid", true, "email", request.getEmail()));
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage("OTP không hợp lệ hoặc đã hết hạn");
                baseResponse.setData(Map.of("valid", false, "email", request.getEmail()));
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(baseResponse);
    }

    /**
     * API đặt lại mật khẩu
     */
    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        BaseResponse baseResponse = new BaseResponse();

        try {
            boolean success = accountService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
            );

            if (success) {
                baseResponse.setCode(200);
                baseResponse.setMessage("Đặt lại mật khẩu thành công");
                baseResponse.setData(Map.of("success", true, "email", request.getEmail()));
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage("OTP không hợp lệ hoặc email không tồn tại");
                baseResponse.setData(Map.of("success", false, "email", request.getEmail()));
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(baseResponse);
    }

    @PostMapping("/auth/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam String email) {
        BaseResponse baseResponse = new BaseResponse();
        
        try {
            boolean success = accountService.resendVerificationEmail(email);
            
            if (success) {
                baseResponse.setCode(200);
                baseResponse.setMessage("Đã gửi lại email xác thực thành công");
                baseResponse.setData(Map.of("email", email));
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage("Email không tồn tại hoặc đã hết hạn");
                baseResponse.setData(null);
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
        }
        
        return ResponseEntity.ok(baseResponse);
    }

    /**
     * API logout - thêm token vào blacklist
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        BaseResponse baseResponse = new BaseResponse();
        
        try {
            // Lấy token từ header Authorization
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // Bỏ "Bearer " prefix
            }
            
            if (token == null || token.trim().isEmpty()) {
                baseResponse.setCode(400);
                baseResponse.setMessage("Token không được cung cấp");
                baseResponse.setData(null);
                return ResponseEntity.badRequest().body(baseResponse);
            }
            
            boolean success = accountService.logout(token);
            
            if (success) {
                baseResponse.setCode(200);
                baseResponse.setMessage("Đăng xuất thành công");
                baseResponse.setData(Map.of("loggedOut", true));
            } else {
                baseResponse.setCode(400);
                baseResponse.setMessage("Token không hợp lệ");
                baseResponse.setData(Map.of("loggedOut", false));
            }
        } catch (Exception e) {
            baseResponse.setCode(500);
            baseResponse.setMessage("Có lỗi xảy ra: " + e.getMessage());
        }
        
        return ResponseEntity.ok(baseResponse);
    }

}
