    package com.example.cinema_booking.model;

    import jakarta.persistence.*;
    import lombok.Data;
    import org.hibernate.annotations.Cascade;
    import org.hibernate.annotations.CascadeType;

    import java.sql.Timestamp;
    import java.time.LocalDateTime;

    @Entity
    @Data
    public class Showtime {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "start_time", nullable = false)
        private LocalDateTime startTime;

        @Column(name = "end_time", nullable = false)
        private LocalDateTime endTime;

        @ManyToOne
        @JoinColumn(name = "movie_id", nullable = false)
        private Movie movie;

        @ManyToOne
        @JoinColumn(name = "room_id", nullable = false)
        private Room room;

        @Column(name = "status")
        private String status; // UPCOMING, ENDED...
    }
