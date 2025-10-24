package com.example.cinema_booking.repository;

import com.example.cinema_booking.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {

    Optional<Showtime> findById(int id);

    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
            "AND (:startTime < s.endTime AND :endTime > s.startTime)")
    List<Showtime> findConflictingShowtimes(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
            "AND (:startTime < s.endTime AND :endTime > s.startTime) AND s.id <> :showtimeId")
    List<Showtime> findConflictingShowtimesForUpdate(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("showtimeId") int showtimeId
    );
    List<Showtime> findByMovieId(Integer movieId);

}
