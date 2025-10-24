package com.example.cinema_booking.controller;

import com.example.cinema_booking.repository.ShiftRepository;
import com.example.cinema_booking.dto.request.ShiftRequest;
import com.example.cinema_booking.dto.request.StaffShiftRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;
//    @Autowired
//    private ShiftRepository shiftRepository;


    @PostMapping("/shift")
    public ResponseEntity<?> addShift(@Valid @RequestBody ShiftRequest shift) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.addShift(shift));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shift")
    public ResponseEntity<?> getAllShifts(@RequestParam int pageNumber, @RequestParam int pageSize) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.getAllShifts(pageNumber, pageSize));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<?> getShiftById(@PathVariable int shiftId) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.getShift(shiftId));
        return ResponseEntity.ok(response);
    }


    @PutMapping("/shift/{shiftId}")
    public ResponseEntity<?> updateShift(@PathVariable int shiftId, @Valid @RequestBody ShiftRequest shift) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Update Successful");
        response.setData(shiftService.updateShift(shiftId, shift));
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/shift/{shiftId}")
    public ResponseEntity<?> deleteShift(@PathVariable int shiftId) {
        shiftService.deleteShift(shiftId);
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Delete Successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/shift/assign")
    public ResponseEntity<?> assignShift(@Valid @RequestBody StaffShiftRequest staffShiftRequest, @RequestParam int staffId, @RequestParam int shiftId) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.assignShift(staffShiftRequest, staffId, shiftId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shift/account")
    public ResponseEntity<?> getShiftsByAccountId(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            BaseResponse response = new BaseResponse();
            response.setCode(200);
            response.setMessage("Success");
            response.setData(shiftService.getShiftsByAccountId());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            BaseResponse response = new BaseResponse();
            response.setCode(404);
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setCode(500);
            response.setMessage("Lỗi hệ thống: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/shift/account/{shiftId}")
    public ResponseEntity<?> getAccountByShiftId(@PathVariable int shiftId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.getAccountByShiftId(shiftId, date));
        return ResponseEntity.ok(response);
    }

    @PostMapping("shift/register/{accountId}")
    public ResponseEntity<?> registerShift(@PathVariable int accountId, @Valid @RequestBody ShiftRequest shift) {
        return null;
    }

    @GetMapping("shift/schedule")
    public ResponseEntity<?> getStaffShiftSchedule(@AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @GetMapping("/shift/assignment-count")
    public ResponseEntity<?> getShiftAssignmentCountByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.getShiftAssignmentCountByDate(date));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shift/assignment-count/one")
    public ResponseEntity<?> getShiftAssignmentCountByDateOne(@RequestParam int shiftId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(shiftService.getShiftAssignmentCountByDate(shiftId, date));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/shift/old")
    public ResponseEntity<?> deleteOldShifts() {
        try {
            shiftService.deleteOldShifts();
            BaseResponse response = new BaseResponse();
            response.setCode(200);
            response.setMessage("Đã xóa lịch ca cũ thành công");
            response.setData(null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setCode(500);
            response.setMessage("Lỗi khi xóa lịch ca cũ: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }
}
