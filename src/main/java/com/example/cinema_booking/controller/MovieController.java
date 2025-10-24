package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.MovieCategoryRequest;
import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // Lấy tất cả phim
    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        try {
            List<MovieResponseDTO> movies = movieService.getAllMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Lấy phim theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovieById(@PathVariable int id) {
        try {
            MovieResponseDTO movie = movieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tạo phim mới
    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO movieRequestDTO) {
        MovieResponseDTO createdMovie = movieService.createMovie(movieRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    // Cập nhật phim
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable int id,
                                                        @Valid @RequestBody MovieRequestDTO movieRequestDTO) {
        try {
            MovieResponseDTO updatedMovie = movieService.updateMovie(id, movieRequestDTO);
            return ResponseEntity.ok(updatedMovie);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Xóa phim
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable int id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tìm phim theo tên
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDTO>> searchMovies(@RequestParam String name) {
        try {
            List<MovieResponseDTO> movies = movieService.searchMoviesByName(name);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Lấy phim sắp chiếu
    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieResponseDTO>> getComingSoonMovies() {
        try {
            List<MovieResponseDTO> movies = movieService.getComingSoonMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieResponseDTO>> getNowShowingMovies() {
        try {
            List<MovieResponseDTO> movies = movieService.getNowShowingMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Nhập phim từ file excel
    @PostMapping("/import-excel")
    public ResponseEntity<String> importMoviesFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            movieService.importMoviesFromExcel(file);
            return ResponseEntity.ok("Import movies successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Import movies failed: " + e.getMessage());
        }
    }

    @PostMapping("/add-category")
    public ResponseEntity<MovieResponseDTO> addMovieCategory(@Valid @RequestBody MovieCategoryRequest movieCategoryRequest) {
        MovieResponseDTO categoryAdded = movieService.addMovieCategory(movieCategoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryAdded);
    }
}
