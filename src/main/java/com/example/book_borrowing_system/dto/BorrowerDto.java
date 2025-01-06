package com.example.book_borrowing_system.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;



public class BorrowerDto {
    @Data
    @Getter
    @Setter
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is mandatory")
        private String email;

    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
    }
}