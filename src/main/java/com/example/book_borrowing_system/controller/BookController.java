package com.example.book_borrowing_system.controller;

import com.example.book_borrowing_system.dto.*;
import com.example.book_borrowing_system.model.*;
import com.example.book_borrowing_system.repository.BookBorrowRepository;
import com.example.book_borrowing_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookBorrowRepository bookBorrowRepository;

    @PostMapping
    public ResponseEntity<BookDto.Response> registerBook(
            @Valid @RequestBody BookDto.RegisterRequest request) {
        Book book = bookService.registerBook(
                request.getIsbn(),
                request.getTitle(),
                request.getAuthor()
        );
        return ResponseEntity.ok(convertToResponse(book));
    }

    @GetMapping
    public ResponseEntity<List<BookDto.Response>> getBooks(
            @RequestParam(required = false) String isbn,  // Optional bookId
            @RequestParam(required = false, defaultValue = "false") boolean withBorrowHistory) {

        List<BookDto.Response> response;

        if (isbn != null) {
            // If a specific book isbn is provided, returns matching books
            Book book = bookService.getBookByIsbn(isbn);
            response = List.of(convertToResponse(book, withBorrowHistory));
        } else {
            List<Book> books = bookService.getAllBooks();
            response = books.stream()
                    .map(book -> convertToResponse(book, withBorrowHistory))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(response);
    }

    private BookDto.Response convertToResponse(Book book, boolean withBorrowHistory) {
        BookDto.Response response = new BookDto.Response();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setAvailable(book.isAvailable());

        if (!book.isAvailable()) {
            BookBorrowDto.Response borrowDetails = bookService.getBookBorrowDetails(book.getId());
            response.setOverdue(borrowDetails.isOverdue());
            response.setExpectedReturnDate(borrowDetails.getExpectedReturnDate());
        }

        if (withBorrowHistory) {
            // Add borrow history if requested
            List<BookDto.BorrowHistoryResponse> borrowHistory = bookBorrowRepository
                    .findByBookId(book.getId())
                    .stream()
                    .map(borrow -> {
                        BookDto.BorrowHistoryResponse history = new BookDto.BorrowHistoryResponse();
                        history.setBorrowId(borrow.getId());
                        history.setBorrowerId(borrow.getBorrower().getId());
                        history.setBorrowerName(borrow.getBorrower().getName());
                        history.setBorrowerEmail(borrow.getBorrower().getEmail());
                        history.setBorrowDate(borrow.getBorrowDate());
                        history.setReturnDate(borrow.getReturnDate());

                        return history;
                    })
                    .collect(Collectors.toList());
            response.setBorrowHistory(borrowHistory);
        }

        return response;
    }


    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<BookBorrowDto.Response> borrowBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookBorrowDto.BorrowRequest request) {
        BookBorrow bookBorrow = bookService.borrowBook(request.getBorrowerId(), bookId);
        return ResponseEntity.ok(convertToResponse(bookBorrow));
    }

    @PostMapping("/{bookId}/return")
    public ResponseEntity<BookBorrowDto.Response> returnBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookBorrowDto.BorrowRequest request) {
        BookBorrow bookBorrow = bookService.returnBook(request.getBorrowerId(), bookId);
        return ResponseEntity.ok(convertToResponse(bookBorrow));
    }

    private BookDto.Response convertToResponse(Book book) {
        BookDto.Response response = new BookDto.Response();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setAvailable(book.isAvailable());
        return response;
    }

    private BookBorrowDto.Response convertToResponse(BookBorrow bookBorrow) {
        BookBorrowDto.Response response = new BookBorrowDto.Response();
        response.setId(bookBorrow.getId());
        response.setBorrowerId(bookBorrow.getBorrower().getId());
        response.setBookId(bookBorrow.getBook().getId());
        response.setBorrowDate(bookBorrow.getBorrowDate());
        response.setReturnDate(bookBorrow.getReturnDate());
        return response;
    }
}