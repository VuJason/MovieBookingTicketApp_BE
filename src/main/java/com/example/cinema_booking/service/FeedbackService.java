package com.example.cinema_booking.service;

import com.example.cinema_booking.model.*;
import com.example.cinema_booking.repository.AccountRepository;
import com.example.cinema_booking.repository.FeedbackRepository;
import com.example.cinema_booking.repository.MovieRepository;
import com.example.cinema_booking.dto.request.FeedbackRequest;
import com.example.cinema_booking.dto.response.FeedbackResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private Validator validator;

    @Transactional
    public Feedback addFeedback(FeedbackRequest feedbackRequest) {

        Movie movie = movieRepository.findById(feedbackRequest.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        Account account = accountRepository.findById(feedbackRequest.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Feedback feedback = new Feedback();
        feedback.setMovie(movie);
        feedback.setAcconut(account);
        feedback.setComment(feedbackRequest.getComment());
        feedback.setStar(feedbackRequest.getStar());

        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }

        return feedbackRepository.save(feedback);
    }

    public List<FeedbackResponse> getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        //return showtimeRepository.findAll();
    }

    public FeedbackResponse getFeedbackById(int feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id : "+feedbackId));
        Movie movie = feedback.getMovie();
        String comment = feedback.getComment();
        Account account = feedback.getAcconut();
        int star = feedback.getStar();

        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }

        return new FeedbackResponse(feedbackId, star, comment, movie, account);
    }

    //Theo logic, update feedback không nên tồn tại vì nó conflict với tự do ngôn luận của khách
    public Feedback updateFeedback(int feedbackId, FeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id : "+feedbackId));
        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }
        feedback.setComment(request.getComment());

        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(int feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new IllegalArgumentException("Feedback not found with id : "+feedbackId));
        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }
        feedbackRepository.delete(feedback);
    }

    // Entity to DTO for List all Showtime
    private FeedbackResponse convertToResponseDTO(Feedback feedback) {
        FeedbackResponse responseDTO = new FeedbackResponse();
        responseDTO.setId(feedback.getId());
        responseDTO.setMovie(feedback.getMovie());
        responseDTO.setComment(feedback.getComment());
        responseDTO.setAccount(feedback.getAcconut());
        responseDTO.setStar(feedback.getStar());
        return responseDTO;
    }
}
