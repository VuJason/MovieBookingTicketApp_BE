package com.example.cinema_booking.service.imp;

import com.example.cinema_booking.model.Movie;
import com.example.cinema_booking.model.Room;
import com.example.cinema_booking.model.Showtime;
import com.example.cinema_booking.repository.MovieRepository;
import com.example.cinema_booking.repository.RoomRepository;
import com.example.cinema_booking.repository.ShowtimeRepository;
import com.example.cinema_booking.dto.request.ShowtimeRequest;
import com.example.cinema_booking.dto.response.ShowtimeResponseDTO;
import com.example.cinema_booking.service.ShowtimeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowtimeServiceImp implements ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    @Transactional
    public Showtime addShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        //Plus minute for End Time
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        // Kiểm tra thời gian hợp lệ
//        if (request.getStartTime().isAfter(endTime)) {
//            throw new IllegalArgumentException("Start time must be before end time.");
//        }

        // Kiểm tra trùng giờ
        List<Showtime> conflicts = showtimeRepository.findConflictingShowtimes(
                room.getId(), request.getStartTime(), endTime
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("The room is already booked during the selected time.");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(endTime);

        return showtimeRepository.save(showtime);
    }

    @Override
    public List<ShowtimeResponseDTO> getAllShowtime() {
        return showtimeRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<ShowtimeResponseDTO> getShowtimesByMovieId(int movieId) {
        return showtimeRepository.findByMovieId(movieId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Showtime updateShowtime(int showtimeId, ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        //Plus minute for End Time
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

//        if (request.getStartTime().isAfter(request.getEndTime())) {
//            throw new IllegalArgumentException("Start time must be before end time.");
//        }

        List<Showtime> conflicts = showtimeRepository.findConflictingShowtimesForUpdate(
                request.getRoomId(), request.getStartTime(), endTime, showtimeId
        );
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("The room is already booked during the selected time.");
        }

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(endTime);

        return showtimeRepository.save(showtime);
    }

    @Override
    public void deleteShowtime(int showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found"));
        showtimeRepository.delete(showtime);
    }

    private ShowtimeResponseDTO convertToResponseDTO(Showtime showtime) {
        ShowtimeResponseDTO dto = new ShowtimeResponseDTO();
        dto.setId(showtime.getId());
        dto.setMovie(showtime.getMovie());
        dto.setRoom(showtime.getRoom());
        dto.setStartTime(showtime.getStartTime());
        dto.setEndTime(showtime.getEndTime());
        return dto;
    }
}