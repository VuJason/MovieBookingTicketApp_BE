package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.ReviewRequest;
import com.example.cinema_booking.dto.response.ReviewResponse;
import com.example.cinema_booking.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    // Lấy tất cả review
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
            List<ReviewResponse> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
    }

    // Lấy review theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable int id) {
        try {
            ReviewResponse review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tạo review mới
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        try {
            ReviewResponse createdReview = reviewService.createReview(reviewRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật review
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable int id,
                                                     @Valid @RequestBody ReviewRequest reviewRequest) {
        try {
            ReviewResponse updatedReview = reviewService.updateReview(id, reviewRequest);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Xóa review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable int id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tìm review theo title
    @GetMapping("/search")
    public ResponseEntity<List<ReviewResponse>> searchReview(@RequestParam String title) {
        try {
            List<ReviewResponse> reviews = reviewService.searchReviewByTitle(title);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
