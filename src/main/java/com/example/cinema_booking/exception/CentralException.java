package com.example.cinema_booking.exception;

import com.example.cinema_booking.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CentralException {
    @ExceptionHandler(InsertException.class)
    public ResponseEntity<?> centralLogRegister(Exception e) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(HttpStatus.BAD_REQUEST.value());
        baseResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> centralLogEntityNotFound(Exception e) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(HttpStatus.NOT_FOUND.value());
        baseResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(baseResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("Validation failed");
        baseResponse.setCode(HttpStatus.BAD_REQUEST.value());
        baseResponse.setData(errors);
        return ResponseEntity.badRequest().body(baseResponse);
    }


}
