package com.example.cinema_booking.service;

import com.example.cinema_booking.model.Event;
import com.example.cinema_booking.repository.EventRepository;
import com.example.cinema_booking.dto.request.EventRequest;
import com.example.cinema_booking.dto.response.EventResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Validator validator;

    // Lấy tất cả event
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy event theo ID
    public EventResponse getEventById(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id : "+id));
        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }
//        if (event.isPresent()) {
//            return this.convertToResponseDTO(event.get());
//        }
        return this.convertToResponseDTO(event);
    }

    // Tạo event mới
    public EventResponse createEvent(EventRequest eventRequest) {
        // exist event
        Optional<Event> existingEvent = eventRepository.findByTitle(eventRequest.getTitle());
        if (existingEvent.isPresent()) {
            throw new IllegalArgumentException("Event with title '" + eventRequest.getTitle() + "' already exists");
        }
        Event event = convertToEntity(eventRequest);

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }

        Event savedEvent = eventRepository.save(event);
        return convertToResponseDTO(savedEvent);
    }

    // Cập nhật event
    public EventResponse updateEvent(int id, EventRequest eventRequest) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (!existingEvent.isPresent()) {
            throw new IllegalArgumentException("Event not found with id: " + id);
        }

        Set<ConstraintViolation<Optional<Event>>> violations = validator.validate(existingEvent);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }

        Event event = existingEvent.get();
        updateEventFields(event, eventRequest);

        Event updatedEvent = eventRepository.save(event);
        return convertToResponseDTO(updatedEvent);
    }

    // Xóa event
    public void deleteEvent(int id) {
        Optional<Event> event = eventRepository.findById(id);
        if (!event.isPresent()) {
            throw new IllegalArgumentException("Event not found with id: " + id);
        }
        Set<ConstraintViolation<Optional<Event>>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }
        eventRepository.deleteById(id);
    }

    // Tìm event theo title
    public List<EventResponse> searchEventByTitle(String title) {
        List<Event> events = eventRepository.findByTitleIgnoreCase(title);
        return events.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi Entity sang ResponseDTO
    private EventResponse convertToResponseDTO(Event event) {
        EventResponse responseDTO = new EventResponse();
        responseDTO.setId(event.getId());
        responseDTO.setTitle(event.getTitle());
        responseDTO.setEventDescription(event.getEventDescription());
        responseDTO.setStartDate(event.getStartDate());
        responseDTO.setEndDate(event.getEndDate());
        //responseDTO.setEventCondition(event.getEventCondition());
        responseDTO.setEventPosterUrl(event.getEventPosterUrl());
        return responseDTO;
    }

    // Chuyển đổi RequestDTO sang Entity
    private Event convertToEntity(EventRequest requestDTO) {
        Event event = new Event();
        event.setTitle(requestDTO.getTitle());
        event.setEventDescription(requestDTO.getEventDescription());
        event.setStartDate(requestDTO.getStartDate());
        event.setEndDate(requestDTO.getEndDate());
        //event.setEventCondition(requestDTO.getEventCondition());
        event.setEventPosterUrl(requestDTO.getEventPosterUrl());
        return event;
    }

    //Update Event Fields
    private void updateEventFields(Event event, EventRequest requestDTO) {
        event.setTitle(requestDTO.getTitle());
        event.setEventDescription(requestDTO.getEventDescription());
        event.setStartDate(requestDTO.getStartDate());
        event.setEndDate(requestDTO.getEndDate());
        //event.setEventCondition(requestDTO.getEventCondition());
        event.setEventPosterUrl(requestDTO.getEventPosterUrl());
    }
}
