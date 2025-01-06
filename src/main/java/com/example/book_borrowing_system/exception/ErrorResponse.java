package com.example.book_borrowing_system.exception;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Object details;
    private String path;


    public static ErrorResponse of(LocalDateTime timestamp, int status, String error, String message, Object details, String path) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(timestamp);
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setDetails(details);
        response.setPath(path);
        return response;
    }
}