package com.example.cinema_booking.dto.request;

import com.example.cinema_booking.validation.ValidDateRange;
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
@ValidDateRange(startDateField = "startDate", endDateField = "endDate", message = "End date must be after start date")
public class EventRequest {
    @NotBlank(message = "Title can not be blank")
    private String title;
    private String eventDescription;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate endDate;
//    @NotBlank(message = "Condition can not be blank")
//    private String eventCondition;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpeg|jpg|gif|bmp|webp))$",
            message = "Must be a valid image URL ending with .png, .jpeg, .jpg, .gif, .bmp, or .webp"
    )
    private String eventPosterUrl;
}
