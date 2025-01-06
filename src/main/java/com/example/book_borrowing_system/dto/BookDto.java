package com.example.book_borrowing_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


public class BookDto {
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "ISBN is required")
        private String isbn;

        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Author is required")
        private String author;
    }

    @Data
    public static class Response {
        private Long id;
        private String isbn;
        private String title;
        private String author;
        private boolean available;
        private List<BorrowHistoryResponse> borrowHistory;
        private boolean isOverdue;
        private LocalDateTime expectedReturnDate;
    }
    
    

    @Data
    public static class BorrowHistoryResponse {
        private long borrowId;
        private long borrowerId;
        private String borrowerName;
        private String borrowerEmail;
        private LocalDateTime borrowDate;
        private LocalDateTime returnDate;
    }
    
}
