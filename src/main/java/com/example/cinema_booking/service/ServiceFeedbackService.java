package com.example.cinema_booking.service;

import com.example.cinema_booking.exception.InsertException;
import com.example.cinema_booking.model.ServiceFeedback;
import com.example.cinema_booking.repository.ServiceFeedBackRepository;
import com.example.cinema_booking.dto.request.ServiceFeedbackRequest;
import com.example.cinema_booking.dto.response.ServiceFeedbackResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceFeedbackService {
    @Autowired
    private ServiceFeedBackRepository serviceFeedbackRepository;

    public ServiceFeedbackResponse addServiceFeedback(ServiceFeedbackRequest serviceFeedbackRequest) {

        try {
            ServiceFeedback serviceFeedback = new ServiceFeedback();
            serviceFeedback.setComment(serviceFeedbackRequest.getComment());
            serviceFeedbackRepository.save(serviceFeedback);

            ServiceFeedbackResponse response = new ServiceFeedbackResponse();
            response.setId(serviceFeedback.getId());
            response.setComment(serviceFeedback.getComment());
            return response;
        } catch (Exception e) {
            throw new InsertException(e.getMessage());
        }
    }

    public List<ServiceFeedbackResponse> getAllServiceFeedback() {
        List<ServiceFeedback> serviceFeedbacks = serviceFeedbackRepository.findAll();
        return serviceFeedbacks.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        //return showtimeRepository.findAll();
    }

    public ServiceFeedbackResponse getServiceFeedbackById(int serviceFeedbackId) {
        ServiceFeedback serviceFeedback = serviceFeedbackRepository.findById(serviceFeedbackId)
                .orElseThrow(() -> new RuntimeException("Service Feedback not found"));
        String comment = serviceFeedback.getComment();
        return new ServiceFeedbackResponse(serviceFeedbackId, comment);
    }

    //Theo logic, update feedback không nên tồn tại vì nó conflict với tự do ngôn luận của khách
    public ServiceFeedback updateServiceFeedback(int serviceFeedbackId, ServiceFeedbackRequest request) {
        ServiceFeedback serviceFeedback = serviceFeedbackRepository.findById(serviceFeedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Service feedback not found"));

        serviceFeedback.setComment(request.getComment());

        return serviceFeedbackRepository.save(serviceFeedback);
    }

    public void deleteServiceFeedback(int serviceFeedbackId) {
        Optional<ServiceFeedback> serviceFeedback = serviceFeedbackRepository.findById(serviceFeedbackId);
        if(serviceFeedback.isPresent()) {
            serviceFeedbackRepository.delete(serviceFeedback.get());
        }
    }

    // Entity to DTO for List all Showtime
    private ServiceFeedbackResponse convertToResponseDTO(ServiceFeedback serviceFeedback) {
        ServiceFeedbackResponse responseDTO = new ServiceFeedbackResponse();
        responseDTO.setId(serviceFeedback.getId());
        responseDTO.setComment(serviceFeedback.getComment());
        return responseDTO;
    }
}
