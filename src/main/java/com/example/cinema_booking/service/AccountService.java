package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.response.GoogleTokenResponse;
import com.example.cinema_booking.exception.InsertException;
import com.example.cinema_booking.model.Account;
import com.example.cinema_booking.model.Role;
import com.example.cinema_booking.repository.AccountRepository;
import com.example.cinema_booking.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import com.example.cinema_booking.dto.request.AccountUpdateRequest;
import com.example.cinema_booking.dto.request.PersonalInfoUpdateRequest;
import com.example.cinema_booking.dto.request.RegisterRequest;
import com.example.cinema_booking.dto.response.AccountResponse;
import com.example.cinema_booking.dto.response.ForgotPasswordResponse;
import com.example.cinema_booking.dto.response.RegisterResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import javax.crypto.SecretKey;
import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.security.SecureRandom;

@Service
public class AccountService {

    // Cache tạm thời để lưu thông tin đăng ký
    private final ConcurrentHashMap<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();
    
    // Class để lưu thông tin đăng ký tạm thời
    private static class PendingRegistration {
        private final RegisterRequest request;
        private final String verificationToken;
        private final LocalDateTime createdAt;
        
        public PendingRegistration(RegisterRequest request, String verificationToken) {
            this.request = request;
            this.verificationToken = verificationToken;
            this.createdAt = LocalDateTime.now();
        }
        
        public RegisterRequest getRequest() { return request; }
        public String getVerificationToken() { return verificationToken; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.key}")
    private String keyJWT;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;




    public RegisterResponse insertUser(RegisterRequest registerRequest) {
        Optional<Account> existedEmail = accountRepository.findByEmail(registerRequest.getEmail());
        if (existedEmail.isPresent()) {
            throw new InsertException("Email already exists");
        }

        try {
            String encryptedPass = passwordEncoder.encode(registerRequest.getPassword());
            Account account = new Account();
            Optional<Role> role = roleRepository.findById(1);
            account.setPassword(encryptedPass);
            account.setEmail(registerRequest.getEmail());
            account.setFullName(registerRequest.getFullName());
            account.setDateOfBirth(registerRequest.getDateOfBirth());
            account.setPhone(registerRequest.getPhone());
            account.setSex(registerRequest.getSex());
            account.setRole(role.get());

            account = accountRepository.save(account);
            
            RegisterResponse response = new RegisterResponse();
            response.setId(account.getId());
            response.setFullName(account.getFullName());
            response.setPassword(account.getPassword());
            response.setEmail(account.getEmail());
            response.setPhone(account.getPhone());
            response.setSex(account.getSex());
            response.setDateOfBirth(account.getDateOfBirth());
            response.setRole(account.getRole());
            
            return response;
        } catch(Exception e) {
            throw new InsertException(e.getMessage());
        }

    }

    public String login(String email, String password) {
        String token = "";
        Optional<Account> account = accountRepository.findByEmail(email);
        if(account.isPresent()) {
            Account ac = account.get();
            
            
            if(passwordEncoder.matches(password, account.get().getPassword())) {
                SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyJWT));
                String jws = Jwts.builder()
                    .subject(ac.getRole().getName())
                    .claim("accountId", ac.getId())  // Thêm accountId vào token
                    .signWith(key)
                    .compact();
                token = jws;
            }
        }
        return token;
    }

    public RegisterResponse createUser(RegisterRequest registerRequest) {
        Optional<Account> existedEmail = accountRepository.findByEmail(registerRequest.getEmail());
        if (existedEmail.isPresent()) {
            throw new InsertException("Email already exists");
        }

        try {
            String encryptedPass = passwordEncoder.encode(registerRequest.getPassword());
            Account account = new Account();
            account.setPassword(encryptedPass);
            account.setEmail(registerRequest.getEmail());
            account.setFullName(registerRequest.getFullName());
            account.setDateOfBirth(registerRequest.getDateOfBirth());
            account.setPhone(registerRequest.getPhone());
            account.setSex(registerRequest.getSex());
            Optional<Role> role = roleRepository.findByName("CUSTOMER");
            account.setRole(role.get());
            account = accountRepository.save(account);
            
            RegisterResponse response = new RegisterResponse();
            response.setId(account.getId());
            response.setFullName(account.getFullName());
            response.setPassword(account.getPassword());
            response.setEmail(account.getEmail());
            response.setPhone(account.getPhone());
            response.setSex(account.getSex());
            response.setDateOfBirth(account.getDateOfBirth());
            response.setRole(account.getRole());
            
            return response;
        } catch(Exception e) {
            throw new InsertException(e.getMessage());
        }

    }

    public List<AccountResponse> getAllAccounts(int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        List<Account> accounts = accountRepository.findAll(page).getContent();
        return accounts.stream().map(account -> {
            AccountResponse response = new AccountResponse();
            response.setAccountId(account.getId());
            response.setAccountName(account.getFullName());
            response.setEmail(account.getEmail());
            response.setRole(account.getRole());
            return response;
        }).toList();
    }

    public void updateAccount(int accountId, AccountUpdateRequest accountUpdateRequest) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()) {
            Account ac = account.get();
            ac.setFullName(accountUpdateRequest.getFullName());
            ac.setPhone(accountUpdateRequest.getPhone());
            ac.setSex(accountUpdateRequest.getSex());
            if(accountUpdateRequest.getRoleId() != 0) {
                try {
                    Role role = roleRepository.findById(accountUpdateRequest.getRoleId()).orElseThrow(() -> new RoleNotFoundException("Role not found"));
                    ac.setRole(role);
                } catch (RoleNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            accountRepository.save(ac);
        }else {
            throw new RuntimeException("Account with ID " + accountId + " not found");
        }
    }

    public void deleteAccount(int accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()) {
            accountRepository.delete(account.get());
        }
    }

    /**
     * Lấy thông tin tài khoản theo ID
     * @param accountId ID của tài khoản
     * @return AccountResponse chứa thông tin tài khoản
     */
    public AccountResponse getAccountById() {
        int accountId = (int) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại với ID: " + accountId));
        
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getId());
        response.setAccountName(account.getFullName());
        response.setEmail(account.getEmail());
        response.setRole(account.getRole());
        
        return response;
    }

    /**
     * Cập nhật tên tài khoản hiện tại
     * @param request Thông tin cần cập nhật
     * @return AccountResponse chứa thông tin tài khoản sau khi cập nhật
     */
    public AccountResponse updatePersonalInfo(PersonalInfoUpdateRequest request) {
        int accountId = (int) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại với ID: " + accountId));
        
        // Cập nhật tên
        account.setFullName(request.getFullName());
        
        // Lưu vào database
        account = accountRepository.save(account);
        
        // Trả về response
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getId());
        response.setAccountName(account.getFullName());
        response.setEmail(account.getEmail());
        response.setRole(account.getRole());
        
        return response;
    }

    public String createOrUpdateGoogleUser(GoogleTokenResponse tokenResponse) {
        Optional<Account> existingAccount = accountRepository.findByEmail(tokenResponse.getEmail());
        Account account;
        if (existingAccount.isEmpty()) {
            account = new Account();
            account.setEmail(tokenResponse.getEmail());
            account.setFullName(tokenResponse.getName());
            Optional<Role> customerRole = roleRepository.findById(1);
            account.setRole(customerRole.orElse(null));
        } else {
            account = existingAccount.get();
            account.setFullName(tokenResponse.getName());
        }
        accountRepository.save(account);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyJWT));
        String jws = Jwts.builder()
                .subject(account.getRole().getName())
                .claim("email", account.getEmail())
                .signWith(key)
                .compact();
        return jws;
    }

    /**
     * Giải mã token và lấy thông tin Account từ token.
     */
//    public Account getAccountFromToken(String token) {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyJWT));
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            Integer accountId = claims.get("accountId", Integer.class);
//            if (accountId == null) {
//                throw new RuntimeException("Token does not contain accountId");
//            }
//            return accountRepository.findById(accountId)
//                    .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
//        } catch (JwtException e) {
//            throw new RuntimeException("Invalid token: " + e.getMessage());
//        }
//    }
    
    /**
     * Gửi OTP cho chức năng quên mật khẩu
     */
    public ForgotPasswordResponse sendForgotPasswordOtp(String email) {
        // Kiểm tra email có tồn tại trong hệ thống không
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isEmpty()) {
            ForgotPasswordResponse response = new ForgotPasswordResponse();
            response.setSuccess(false);
            response.setMessage("Email không tồn tại trong hệ thống");
            response.setEmail(email);
            return response;
        }
        
        // Tạo OTP
        String otp = otpService.generateOtp(email);
        
        // Gửi email chứa OTP
        String subject = "🔐 Mã OTP Đặt Lại Mật Khẩu";
        String htmlContent = """
                <html>
                <body style='font-family:Arial,sans-serif; max-width:600px; margin:0 auto; padding:20px;'>
                    <div style='background-color:#f8f9fa; padding:30px; border-radius:10px;'>
                        <h2 style='color:#007bff; text-align:center; margin-bottom:30px;'>
                            🎬 Cinema Booking System
                        </h2>
                        <h3 style='color:#28a745; text-align:center; margin-bottom:20px;'>
                            Mã OTP Đặt Lại Mật Khẩu
                        </h3>
                        <div style='background-color:#ffffff; padding:20px; border-radius:8px; border:2px solid #007bff; text-align:center; margin:20px 0;'>
                            <h1 style='color:#007bff; font-size:32px; letter-spacing:5px; margin:0;'>
                                %s
                            </h1>
                        </div>
                        <p style='color:#6c757d; text-align:center; margin-bottom:15px;'>
                            <strong>Mã OTP này có hiệu lực trong 5 phút</strong>
                        </p>
                        <p style='color:#6c757d; text-align:center; margin-bottom:15px;'>
                            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                        </p>
                        <hr style='border:none; border-top:1px solid #dee2e6; margin:20px 0;'>
                        <p style='color:#6c757d; font-size:12px; text-align:center;'>
                            Email này được gửi tự động, vui lòng không trả lời.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(otp);
        
        try {
            emailService.sendHtmlEmail(email, subject, htmlContent);
            
            ForgotPasswordResponse response = new ForgotPasswordResponse();
            response.setSuccess(true);
            response.setMessage("Mã OTP đã được gửi đến email của bạn");
            response.setEmail(email);
            return response;
        } catch (Exception e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse();
            response.setSuccess(false);
            response.setMessage("Không thể gửi email. Vui lòng thử lại sau");
            response.setEmail(email);
            return response;
        }
    }
    
    /**
     * Xác thực OTP
     */
    public boolean verifyOtp(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }
    
    /**
     * Đặt lại mật khẩu
     */
    public boolean resetPassword(String email, String otp, String newPassword) {
        // Xác thực OTP trước
        if (!otpService.verifyOtp(email, otp)) {
            return false;
        }
        
        // Tìm account
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isEmpty()) {
            return false;
        }
        
        // Cập nhật mật khẩu mới
        Account acc = account.get();
        String encryptedPassword = passwordEncoder.encode(newPassword);
        acc.setPassword(encryptedPassword);
        accountRepository.save(acc);
        
        // Xóa OTP sau khi đặt lại mật khẩu thành công
        otpService.removeOtp(email);
        
        return true;
    }

    /**
     * Tạo tài khoản tạm thời và gửi email xác thực
     */
    public RegisterResponse registerWithEmailVerification(RegisterRequest registerRequest) {
        Optional<Account> existedEmail = accountRepository.findByEmail(registerRequest.getEmail());
        if (existedEmail.isPresent()) {
            throw new InsertException("Email already exists");
        }

        try {
            // Tạo UUID token
            String verificationToken = UUID.randomUUID().toString();
            
            // Lưu thông tin đăng ký vào cache tạm thời
            PendingRegistration pendingReg = new PendingRegistration(registerRequest, verificationToken);
            pendingRegistrations.put(registerRequest.getEmail(), pendingReg);
            
            // Gửi email xác thực
            sendVerificationEmail(registerRequest.getEmail(), registerRequest.getFullName(), verificationToken);
            
            RegisterResponse response = new RegisterResponse();
            response.setId(0); // Chưa có ID vì chưa lưu DB
            response.setFullName(registerRequest.getFullName());
            response.setEmail(registerRequest.getEmail());
            response.setPhone(registerRequest.getPhone());
            response.setSex(registerRequest.getSex());
            response.setDateOfBirth(registerRequest.getDateOfBirth());
            response.setRole(null); // Chưa có role vì chưa lưu DB
            
            return response;
        } catch(Exception e) {
            throw new InsertException(e.getMessage());
        }
    }
    
    /**
     * Xác thực email và tạo tài khoản
     */
    public boolean verifyEmail(String email, String token) {
        PendingRegistration pendingReg = pendingRegistrations.get(email);
        
        if (pendingReg == null) {
            return false;
        }
        
        // Kiểm tra token có hết hạn không (24 giờ)
        if (LocalDateTime.now().isAfter(pendingReg.getCreatedAt().plusHours(24))) {
            pendingRegistrations.remove(email);
            return false;
        }
        
        if (!pendingReg.getVerificationToken().equals(token)) {
            return false;
        }
        
        try {
            // Tạo tài khoản mới
            Account account = new Account();
            account.setEmail(pendingReg.getRequest().getEmail());
            
            // Mã hóa password
            String encryptedPassword = passwordEncoder.encode(pendingReg.getRequest().getPassword());
            account.setPassword(encryptedPassword);
            
            account.setFullName(pendingReg.getRequest().getFullName());
            account.setPhone(pendingReg.getRequest().getPhone());
            account.setSex(pendingReg.getRequest().getSex());
            account.setDateOfBirth(pendingReg.getRequest().getDateOfBirth());
            
            // Tự động set role CUSTOMER
            Optional<Role> customerRole = roleRepository.findByName("CUSTOMER");
            if (customerRole.isPresent()) {
                account.setRole(customerRole.get());
            } else {
                throw new RuntimeException("Role CUSTOMER không tồn tại");
            }
            
            // Lưu tài khoản vào database
            accountRepository.save(account);
            
            // Xóa khỏi cache tạm thời
            pendingRegistrations.remove(email);
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo tài khoản: " + e.getMessage());
        }
    }
    
    /**
     * Gửi email xác thực
     */
    private void sendVerificationEmail(String email, String fullName, String token) {
        String subject = "🎬 Xác thực tài khoản Cinema Booking";
        String verificationUrl = "http://localhost:5173/verify-email-link?email=" + email + "&token=" + token;
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Xác thực tài khoản</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        background-color: #2196f3;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        background-color: #f9f9f9;
                        padding: 20px;
                        border-radius: 0 0 5px 5px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #4CAF50;
                        color: white;
                        padding: 12px 24px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 20px;
                        color: #666;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🎬 Cinema Booking System</h1>
                </div>
                
                <div class="content">
                    <p>Xin chào <strong>%s</strong>,</p>
                    
                    <p>Cảm ơn bạn đã đăng ký tài khoản tại Cinema Booking System!</p>
                    
                    <p>Để hoàn tất quá trình đăng ký, vui lòng xác thực email của bạn bằng cách nhấn vào nút bên dưới:</p>
                    
                    <div style="text-align: center;">
                        <a href="%s" class="button">Xác thực tài khoản</a>
                    </div>
                    
                    <p><strong>Lưu ý:</strong> Nút xác thực này có hiệu lực trong 24 giờ.</p>
                    
                    <p>Nếu bạn không thực hiện đăng ký tài khoản này, vui lòng bỏ qua email này.</p>
                    
                    <p>Trân trọng,<br>
                    <strong>Ban quản lý Cinema Booking System</strong></p>
                </div>
                
                <div class="footer">
                    <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                </div>
            </body>
            </html>
            """.formatted(fullName, verificationUrl);
        
        try {
            emailService.sendHtmlEmail(email, subject, htmlContent);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email xác thực: " + e.getMessage());
        }
    }

    /**
     * Cleanup các đăng ký tạm thời hết hạn (chạy mỗi giờ)
     */
    @Scheduled(fixedRate = 3600000) // 1 giờ = 3600000ms
    public void cleanupExpiredRegistrations() {
        LocalDateTime now = LocalDateTime.now();
        pendingRegistrations.entrySet().removeIf(entry -> {
            boolean expired = now.isAfter(entry.getValue().getCreatedAt().plusHours(24));
            if (expired) {
                System.out.println("Đã xóa đăng ký hết hạn cho email: " + entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * Resend email xác thực
     */
    public boolean resendVerificationEmail(String email) {
        PendingRegistration pendingReg = pendingRegistrations.get(email);
        if (pendingReg == null) {
            return false;
        }
        
        try {
            sendVerificationEmail(email, pendingReg.getRequest().getFullName(), pendingReg.getVerificationToken());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Logout - thêm token vào blacklist
     * @param token JWT token cần logout
     * @return true nếu logout thành công
     */
    public boolean logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Thêm token vào blacklist
            tokenBlacklistService.blacklistToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Kiểm tra token có hợp lệ không (không bị blacklist)
     * @param token JWT token cần kiểm tra
     * @return true nếu token hợp lệ, false nếu bị blacklist
     */
    public boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        return !tokenBlacklistService.isTokenBlacklisted(token);
    }
}

