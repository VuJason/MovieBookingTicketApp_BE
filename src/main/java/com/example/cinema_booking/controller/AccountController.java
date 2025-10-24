package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.AccountUpdateRequest;
import com.example.cinema_booking.dto.request.PersonalInfoUpdateRequest;
import com.example.cinema_booking.dto.request.RegisterRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;


    @PostMapping("/account")
    public ResponseEntity<?> createAccount(@RequestBody RegisterRequest registerRequest) {
        BaseResponse response = new BaseResponse();
        response.setMessage("Account created");
        response.setCode(200);
        response.setData(accountService.createUser(registerRequest));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAllAccounts(@RequestParam int pageNumber, @RequestParam int pageSize) {
        BaseResponse response = new BaseResponse();
        response.setMessage("All accounts");
        response.setCode(200);
        response.setData(accountService.getAllAccounts(pageNumber, pageSize));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/details")
    public ResponseEntity<?> getAccountById() {
        try {
            BaseResponse response = new BaseResponse();
            response.setMessage("Account retrieved successfully");
            response.setCode(200);
            response.setData(accountService.getAccountById());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setMessage(e.getMessage());
            response.setCode(404);
            response.setData(null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @PutMapping("/account/update")
    public ResponseEntity<?> updatePersonalInfo(@Valid @RequestBody PersonalInfoUpdateRequest request) {
        try {
            BaseResponse response = new BaseResponse();
            response.setMessage("Thông tin cá nhân đã được cập nhật thành công");
            response.setCode(200);
            response.setData(accountService.updatePersonalInfo(request));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setMessage(e.getMessage());
            response.setCode(400);
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/account/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable int accountId, @RequestBody AccountUpdateRequest accountUpdateRequest) {
        accountService.updateAccount(accountId, accountUpdateRequest);
        BaseResponse response = new BaseResponse();
        response.setMessage("Account updated");
        response.setCode(200);
        return ResponseEntity.ok(response);


    }

    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable int accountId) {
        accountService.deleteAccount(accountId);
        BaseResponse response = new BaseResponse();
        response.setMessage("Account deleted");
        response.setCode(200);
        return ResponseEntity.ok(response);

    }
}
