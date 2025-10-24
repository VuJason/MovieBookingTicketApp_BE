package com.example.cinema_booking.service;

import com.example.cinema_booking.model.Review;
import com.example.cinema_booking.repository.ReviewRepository;
import com.example.cinema_booking.dto.request.ReviewRequest;
import com.example.cinema_booking.dto.response.ReviewResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private Validator validator;

    // Lấy tất cả event
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy review theo ID
    public ReviewResponse getReviewById(int id) {
        Optional<Review> review = reviewRepository.findById(id);
//                .orElseThrow(() -> new IllegalArgumentException("Review not found with id : "+id));
//        Set<ConstraintViolation<Review>>violations = validator.validate(review);
//        if (!violations.isEmpty()) {
//            throw new IllegalArgumentException(
//                    violations.stream()
//                            .map(v -> v.getMessage())
//                            .collect(Collectors.joining(", "))
//            );
//        }
        if (review.isPresent()) {
            return this.convertToResponseDTO(review.get());
        }
        throw new RuntimeException("Event not found with id: " + id);
    }

    // Tạo review mới
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        // exist review
        Optional<Review> existingReview = reviewRepository.findByTitle(reviewRequest.getTitle());
        if (existingReview.isPresent()) {
            throw new RuntimeException("Review with title '" + reviewRequest.getTitle() + "' already exists");
        }
        Set<ConstraintViolation<Optional<Review>>>violations = validator.validate(existingReview);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }
        Review review = convertToEntity(reviewRequest);
        review.setCreateDate(LocalDate.now());
        Review savedReview = reviewRepository.save(review);
        return convertToResponseDTO(savedReview);
    }

    // Cập nhật review
    public ReviewResponse updateReview(int id, ReviewRequest reviewRequest) {
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (!existingReview.isPresent()) {
            throw new RuntimeException("Review not found with id: " + id);
        }

        Set<ConstraintViolation<Optional<Review>>>violations = validator.validate(existingReview);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }

        Review review = existingReview.get();
        updatedReviewFields(review, reviewRequest);

        Review updatedReview = reviewRepository.save(review);
        return convertToResponseDTO(updatedReview);
    }

    // Xóa review
    public void deleteReview(int id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (!review.isPresent()) {
            throw new RuntimeException("Review not found with id: " + id);
        }

        Set<ConstraintViolation<Optional<Review>>>violations = validator.validate(review);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }

        reviewRepository.deleteById(id);
    }

    // Tìm review theo title
    public List<ReviewResponse> searchReviewByTitle(String title) {
        List<Review> reviews = reviewRepository.findByTitleContainingIgnoreCase(title);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi Entity sang ResponseDTO
    private ReviewResponse convertToResponseDTO(Review review) {
        ReviewResponse responseDTO = new ReviewResponse();

        String title = review.getTitle();
        String reviewDescription = review.getReviewDescription();
        String author = review.getAuthor();


        responseDTO.setId(review.getId());
        responseDTO.setTitle(title.trim());
        responseDTO.setReviewDescription(reviewDescription.trim());
        responseDTO.setCreateDate(review.getCreateDate());
        responseDTO.setAuthor(author.trim());
        responseDTO.setReviewPosterUrl(review.getReviewPosterUrl());
        return responseDTO;
    }

    // Chuyển đổi RequestDTO sang Entity
    private Review convertToEntity(ReviewRequest requestDTO) {
        Review review = new Review();
        review.setTitle(requestDTO.getTitle());
        review.setReviewDescription(requestDTO.getReviewDescription());
        //review.setCreateDate(requestDTO.getCreateDate());
        review.setAuthor(requestDTO.getAuthor());
        review.setReviewPosterUrl(requestDTO.getReviewPosterUrl());
        return review;
    }

    //Update Review Fields
    private void updatedReviewFields(Review review, ReviewRequest requestDTO) {
        review.setTitle(requestDTO.getTitle());
        review.setReviewDescription(requestDTO.getReviewDescription());
        //review.setCreateDate(requestDTO.getCreateDate());
        review.setAuthor(requestDTO.getAuthor());
        review.setReviewPosterUrl(requestDTO.getReviewPosterUrl());
    }
}
