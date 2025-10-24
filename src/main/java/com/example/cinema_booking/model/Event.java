package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title can not be blank")
    private String title;

    //KHÔNG ĐẶT TÊN TRƯỜNG THEO TÊN LỆNH QUERY MYSQL
    @Column(columnDefinition = "TEXT")
    private String eventDescription;

//    @NotBlank(message = "Condition can not be blank")
//    @Column(columnDefinition = "TEXT")
//    private String eventCondition;
    //----------------------------------------------

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpeg|jpg|gif|bmp|webp))$",
            message = "Must be a valid image URL ending with .png, .jpeg, .jpg, .gif, .bmp, or .webp"
    )
    private String eventPosterUrl;
}
