package com.example.cinema_booking.repository;

import com.example.cinema_booking.dto.request.MovieCategoryRequest;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.model.Movie;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    // Tìm kiếm theo tên
    List<Movie> findByNameContainingIgnoreCase(String name);
    Optional<Movie> findByName(String name);
    // Tìm phim theo đạo diễn
    List<Movie> findByDirectorContainingIgnoreCase(String director);

    //Tìm kiếm phim sắp chiếu
    List<Movie> findByIsComingSoonTrue();

    // Tìm kiếm phim đang chiếu
    List<Movie> findByIsComingSoonFalse();

    // Tìm kiếm với pagination
    Page<Movie> findByNameContainingIgnoreCase(String name, Pageable pageable);


    List<Movie> findByIsComingSoon(Boolean isComingSoon);
}
