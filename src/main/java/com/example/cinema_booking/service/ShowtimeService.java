

package com.example.cinema_booking.service;

import com.example.cinema_booking.model.Showtime;
import com.example.cinema_booking.dto.request.ShowtimeRequest;
import com.example.cinema_booking.dto.response.ShowtimeResponseDTO;

import java.util.List;

public interface ShowtimeService {
    Showtime addShowtime(ShowtimeRequest showtimeRequest);

    List<ShowtimeResponseDTO> getAllShowtime();

    Showtime updateShowtime(int showtimeId, ShowtimeRequest showtimeRequest);

    void deleteShowtime(int showtimeId);
    List<ShowtimeResponseDTO> getShowtimesByMovieId(int movieId);

}

