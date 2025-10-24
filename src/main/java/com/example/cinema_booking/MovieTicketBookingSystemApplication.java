package com.example.cinema_booking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Cinema API", version = "1.0", description = "Information"))
public class MovieTicketBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieTicketBookingSystemApplication.class, args);
	}

}
