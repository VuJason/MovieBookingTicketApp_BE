package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.FeedbackRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.dto.response.FeedbackResponse;
import com.example.cinema_booking.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

        @PostMapping("/feedback")
    public ResponseEntity<?> addFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            BaseResponse response = new BaseResponse();
            response.setMessage("Feedback created");
            response.setCode(200);
            response.setData(feedbackService.addFeedback(feedbackRequest));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
//            Feedback createdFeedback = feedbackService.addFeedback(feedbackRequest);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
    }

    @GetMapping("/feedback")
    public ResponseEntity<?> getFeedbackById(int feedbackId) {
        try {
            BaseResponse response = new BaseResponse();
            response.setMessage("Feedback(s) with id : "+feedbackId);
            response.setCode(200);
            response.setData(feedbackService.getFeedbackById(feedbackId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/feedback/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable int feedbackId, @Valid @RequestBody FeedbackRequest feedbackRequest) {
        try {
            feedbackService.updateFeedback(feedbackId, feedbackRequest);
            BaseResponse response = new BaseResponse();
            response.setMessage("Feedback updated");
            response.setCode(200);
            response.setData(feedbackService.updateFeedback(feedbackId, feedbackRequest));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/feedback/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(@PathVariable int feedbackId) {
        try {
            feedbackService.deleteFeedback(feedbackId);
            BaseResponse response = new BaseResponse();
            response.setMessage("Feedback deleted");
            response.setCode(200);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/feedback/all")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        List<FeedbackResponse> feedbacks = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbacks);
    }
}
