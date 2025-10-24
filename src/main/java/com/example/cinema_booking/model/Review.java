package com.example.cinema_booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title must not be blank")
    private String title;

    //KHÔNG ĐẶT TÊN TRƯỜNG THEO TÊN LỆNH QUERY MYSQL
    @Column(columnDefinition = "TEXT")
    private String reviewDescription;
    //----------------------------------------------

    @NotBlank(message = "Author can not be blank")
    private String author;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createDate;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpeg|jpg|gif|bmp|webp))$",
            message = "Must be a valid image URL ending with .png, .jpeg, .jpg, .gif, .bmp, or .webp"
    )
    private String reviewPosterUrl;
}
