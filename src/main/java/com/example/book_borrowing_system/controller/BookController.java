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

    /**
     * Registers a new book in the system.
     * Accepts book details (ISBN, title, author) in the request body and returns the registered book details.
     *
     * @param request Book details for registration.
     * @return The registered book's details.
     */
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


    /**
     * Retrieves a list of books. Optionally, a book's ISBN can be provided to fetch a specific book.
     * Also allows fetching borrow history for each book.
     *
     * @param isbn ISBN of the book to fetch (optional).
     * @param withBorrowHistory Flag indicating whether to include borrow history (optional, default is false).
     * @return A list of books with or without borrow history.
     */
    @GetMapping
    public ResponseEntity<List<BookDto.Response>> getBooks(
            @RequestParam(required = false) String isbn,  // Optional bookId
            @RequestParam(required = false, defaultValue = "false") boolean withBorrowHistory) {

        List<BookDto.Response> response;

        if (isbn != null) {
            // Check if a specific book isbn is provided, returns matching books
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

    /**
     * Converts a Book object into a response DTO.
     * Optionally includes borrow history and overdue details.
     *
     * @param book The Book object to convert.
     * @param withBorrowHistory Flag indicating whether to include borrow history.
     * @return The BookDto.Response containing book details and optional borrow history.
     */
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

    /**
     * Allows a borrower to borrow a book by providing the book's ID and borrower's ID.
     *
     * @param bookId The ID of the book to borrow.
     * @param request The borrow request containing borrower ID.
     * @return The details of the book borrow transaction.
     */
    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<BookBorrowDto.Response> borrowBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookBorrowDto.BorrowRequest request) {
        BookBorrow bookBorrow = bookService.borrowBook(request.getBorrowerId(), bookId);
        return ResponseEntity.ok(convertToResponse(bookBorrow));
    }

    /**
     * Allows a borrower to return a borrowed book by providing the book's ID and borrower's ID.
     *
     * @param bookId The ID of the book being returned.
     * @param request The borrow request containing borrower ID.
     * @return The details of the book return transaction.
     */
    @PostMapping("/{bookId}/return")
    public ResponseEntity<BookBorrowDto.Response> returnBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookBorrowDto.BorrowRequest request) {
        BookBorrow bookBorrow = bookService.returnBook(request.getBorrowerId(), bookId);
        return ResponseEntity.ok(convertToResponse(bookBorrow));
    }

    /**
     * Converts a Book object into a response DTO.
     *
     * @param book The Book object to convert.
     * @return The BookDto.Response containing book details.
     */
    private BookDto.Response convertToResponse(Book book) {
        BookDto.Response response = new BookDto.Response();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setAvailable(book.isAvailable());
        return response;
    }

    /**
     * Converts a BookBorrow object into a response DTO.
     *
     * @param bookBorrow The BookBorrow object to convert.
     * @return The BookBorrowDto.Response containing borrow transaction details.
     */
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