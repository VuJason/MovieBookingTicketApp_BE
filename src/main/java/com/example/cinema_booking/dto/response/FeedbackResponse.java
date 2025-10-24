package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.model.Account;
import com.example.cinema_booking.model.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private int id;
    private int star;
    private String comment;
    private Movie movie;
    private Account account;
}
