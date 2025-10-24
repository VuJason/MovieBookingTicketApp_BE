package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.EventRequest;
import com.example.cinema_booking.dto.response.EventResponse;
import com.example.cinema_booking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    // Lấy tất cả event
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        try {
            List<EventResponse> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Lấy event theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable int id) {
        try {
            EventResponse event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tạo event mới
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        EventResponse createdEvent = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    // Cập nhật event
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable int id,
                                                        @Valid @RequestBody EventRequest eventRequest) {
        try {
            EventResponse updatedEvent = eventService.updateEvent(id, eventRequest);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Xóa event
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable int id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tìm event theo title
    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEvent(@RequestParam String title) {
        try {
            List<EventResponse> events = eventService.searchEventByTitle(title);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
