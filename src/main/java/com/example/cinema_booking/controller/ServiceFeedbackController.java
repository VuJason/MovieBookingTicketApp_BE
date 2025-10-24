package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.ServiceFeedbackRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.dto.response.ServiceFeedbackResponse;
import com.example.cinema_booking.service.ServiceFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceFeedbackController {

    @Autowired
    private ServiceFeedbackService serviceFeedbackService;

    @PostMapping("/service")
    public ResponseEntity<?> addServiceFeedback(@RequestBody ServiceFeedbackRequest serviceFeedbackRequest) {
        BaseResponse response = new BaseResponse();
        response.setMessage("Service Feedback created");
        response.setCode(200);
        response.setData(serviceFeedbackService.addServiceFeedback(serviceFeedbackRequest));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service")
    public ResponseEntity<?> getServiceFeedbackById(int serviceFeedbackId) {
        BaseResponse response = new BaseResponse();
        response.setMessage("Service Feedback(s) with id : "+serviceFeedbackId);
        response.setCode(200);
        response.setData(serviceFeedbackService.getServiceFeedbackById(serviceFeedbackId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/service/{serviceFeedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable int serviceFeedbackId, @RequestBody ServiceFeedbackRequest serviceFeedbackRequest) {
        serviceFeedbackService.updateServiceFeedback(serviceFeedbackId, serviceFeedbackRequest);
        BaseResponse response = new BaseResponse();
        response.setMessage("Feedback updated");
        response.setCode(200);
        response.setData(serviceFeedbackService.updateServiceFeedback(serviceFeedbackId, serviceFeedbackRequest));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/service/{serviceFeedbackId}")
    public ResponseEntity<?> deleteServiceFeedback(@PathVariable int serviceFeedbackId) {
        serviceFeedbackService.deleteServiceFeedback(serviceFeedbackId);
        BaseResponse response = new BaseResponse();
        response.setMessage("Service Feedback deleted");
        response.setCode(200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service/all")
    public ResponseEntity<List<ServiceFeedbackResponse>> getAllServiceFeedback() {
        List<ServiceFeedbackResponse> serviceFeedbacks = serviceFeedbackService.getAllServiceFeedback();
        return ResponseEntity.ok(serviceFeedbacks);
    }
}
