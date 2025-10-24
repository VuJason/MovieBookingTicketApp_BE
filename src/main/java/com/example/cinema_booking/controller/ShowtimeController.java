package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.ShowtimeRequest;
import com.example.cinema_booking.dto.response.BaseResponse;
import com.example.cinema_booking.dto.response.ShowtimeResponseDTO;
import com.example.cinema_booking.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;


    @PostMapping("/showtime")
    public ResponseEntity<?> addShowtime(@Valid @RequestBody ShowtimeRequest showtime) {
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(showtimeService.addShowtime(showtime));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/showtime")
    public ResponseEntity<List<ShowtimeResponseDTO>> getAllShowtime() {
//        BaseResponse response = new BaseResponse();
//        response.setCode(200);
//        response.setMessage("Success");
//        response.setData(showtimeService.getAllShowtime());
//        return ResponseEntity.ok(response);
        List<ShowtimeResponseDTO> showtimes = showtimeService.getAllShowtime();
        return ResponseEntity.ok(showtimes);
    }


    @PutMapping("/showtime/{showtimeId}")
    public ResponseEntity<?> updateShowtime(@PathVariable int showtimeId, @Valid @RequestBody ShowtimeRequest showtime) {
        showtimeService.updateShowtime(showtimeId, showtime);
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Update Successful");
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/showtime/{showtimeId}")
    public ResponseEntity<?> deleteShowtime(@PathVariable int showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        BaseResponse response = new BaseResponse();
        response.setCode(200);
        response.setMessage("Delete Successful");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/showtime/movie/{movieId}")
    public ResponseEntity<List<ShowtimeResponseDTO>> getShowtimesByMovie(@PathVariable int movieId) {
        List<ShowtimeResponseDTO> showtimes = showtimeService.getShowtimesByMovieId(movieId);
        return ResponseEntity.ok(showtimes);
    }

}

