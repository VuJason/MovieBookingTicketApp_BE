package com.example.cinema_booking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Description must not be blank")
    private String reviewDescription;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
//    private LocalDate createDate;
    @NotBlank(message = "Author can not be blank")
    private String author;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpeg|jpg|gif|bmp|webp))$",
            message = "Must be a valid image URL ending with .png, .jpeg, .jpg, .gif, .bmp, or .webp"
    )
    private String reviewPosterUrl;
}
