package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.UserUpdateRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

//    @PostMapping("/user")
//    public ResponseEntity<?> createUser(@RequestBody UserRegisterRequest userRegisterRequest) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage("Account created");
//        response.setCode(200);
//        response.setData(userService.createUser(userRegisterRequest));
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserById(@AuthenticationPrincipal Integer userId) {
        BaseResponse response = new BaseResponse();
        response.setMessage("User with id : "+userId);
        response.setCode(200);
        response.setData(userService.getUserById(userId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(userId, userUpdateRequest);
        BaseResponse response = new BaseResponse();
        response.setMessage("User updated");
        response.setCode(200);
        response.setData(userService.updateUser(userId, userUpdateRequest));
        return ResponseEntity.ok(response);


    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        BaseResponse response = new BaseResponse();
        response.setMessage("User deleted");
        response.setCode(200);
        return ResponseEntity.ok(response);

    }
}
