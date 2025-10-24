package com.example.cinema_booking.service;


import com.example.cinema_booking.dto.request.MovieCategoryRequest;
import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MovieService {

    List<MovieResponseDTO> getAllMovies();

    MovieResponseDTO getMovieById(int id);

    MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO);

    MovieResponseDTO updateMovie(int id, MovieRequestDTO movieRequestDTO);

    void deleteMovie(int id);

    List<MovieResponseDTO> searchMoviesByName(String name);

    MovieResponseDTO addMovieCategory(MovieCategoryRequest request);

    List<MovieResponseDTO> getComingSoonMovies();

    List<MovieResponseDTO> getNowShowingMovies();

    void importMoviesFromExcel(MultipartFile file);

}