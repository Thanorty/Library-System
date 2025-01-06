package com.example.book_borrowing_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class BookBorrowDto {
    @Data
    public static class BorrowRequest {
        private Long borrowerId;
        private Long bookId;
    }

    @Data
    public static class Response {
        private Long id;
        private Long borrowerId;
        private Long bookId;
        private LocalDateTime borrowDate;
        private LocalDateTime returnDate;
        private boolean isOverdue; // New field for overdue status
        private LocalDateTime expectedReturnDate; // New field for expected return date
        
    }
}
