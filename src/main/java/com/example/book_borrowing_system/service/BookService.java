package com.example.book_borrowing_system.service;


import com.example.book_borrowing_system.dto.BookBorrowDto;
import com.example.book_borrowing_system.model.*;
import com.example.book_borrowing_system.repository.*;
import com.example.book_borrowing_system.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookBorrowRepository bookBorrowRepository;
    private final BorrowerRepository borrowerRepository;
    

    /**
     * Registers a new book with the given ISBN, title, and author.
     * This method checks if a book with the same ISBN already exists and if its title/author match.
     * If there's a conflict (same ISBN but different title or author), a ConflictException is thrown.
     * @param isbn the ISBN of the book
     * @param title the title of the book
     * @param author the author of the book
     * @return the registered Book object
     */
    @Transactional
    public Book registerBook(String isbn, String title, String author) {
        // Check if a book with the same ISBN already exists
        List<Book> existingBooks = bookRepository.findByIsbn(isbn);
        if (!existingBooks.isEmpty()) {
            for (Book existingBook : existingBooks) {
                if (!existingBook.getTitle().equalsIgnoreCase(title) ||
                        !existingBook.getAuthor().equalsIgnoreCase(author)) {
                    throw new ConflictException(
                            "A book with the same ISBN exists but with different title or author. Please enter valid Author and Title.");
                }
            }
        }

        // Register the new book
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setAvailable(true);
        return bookRepository.save(book);
    }

    /**
     * Retrieves all books from the repository.
     * @return a list of all books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Allows a borrower to borrow a book. This method checks if the book is available and if it's not already borrowed.
     * It marks the book as unavailable and creates a new BookBorrow record.
     * @param borrowerId the ID of the borrower
     * @param bookId the ID of the book to borrow
     * @return the created BookBorrow object
     */
    @Transactional
    public BookBorrow borrowBook(Long borrowerId, Long bookId) {
        // Fetch the book and borrower details
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        // Check if the book is available for borrowing
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        // Check if the book is already borrowed
        if (bookBorrowRepository.findActiveBookBorrow(bookId).isPresent()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        // Mark the book as unavailable and save the book details
        book.setAvailable(false);
        bookRepository.save(book);

        // Create a new BookBorrow record
        BookBorrow bookBorrow = new BookBorrow();
        bookBorrow.setBorrower(borrower);
        bookBorrow.setBook(book);
        bookBorrow.setBorrowDate(LocalDateTime.now());

        // Save the borrow record
        return bookBorrowRepository.save(bookBorrow);
    }

    /**
     * Allows a borrower to return a borrowed book.
     * This method checks if the book was borrowed by the correct borrower and updates the book's availability.
     * @param borrowerId the ID of the borrower
     * @param bookId the ID of the book to return
     * @return the updated BookBorrow object
     */
    @Transactional
    public BookBorrow returnBook(Long borrowerId, Long bookId) {
        // Fetch the active book borrow record
        BookBorrow bookBorrow = bookBorrowRepository.findActiveBookBorrow(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found"));

        // Check if the borrower is returning the correct book
        if (!bookBorrow.getBorrower().getId().equals(borrowerId)) {
            throw new IllegalStateException("Book was not borrowed by this borrower");
        }

        // Mark the book as available and save the book details
        Book book = bookBorrow.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        // Set the return date and save the updated borrow record
        bookBorrow.setReturnDate(LocalDateTime.now());
        return bookBorrowRepository.save(bookBorrow);
    }

    /**
     * Retrieves a book by its ISBN.
     * This method throws a ResourceNotFoundException if the book with the given ISBN is not found.
     * @param isbn the ISBN of the book
     * @return the found Book object
     */
    public Book getBookByIsbn(String isbn) {
        List<Book> books = bookRepository.findByIsbn(isbn);
        if (books.isEmpty()) {
            throw new ResourceNotFoundException("Book not found with ISBN: " + isbn);
        }
        return books.get(0);
    }

    /**
     * Retrieves the borrow details of a book.
     * This method checks if the book has an active borrow record and returns the borrow details, including overdue status.
     * @param bookId the ID of the book
     * @return the borrow details of the book
     */
    @Transactional
    public BookBorrowDto.Response getBookBorrowDetails(Long bookId) {
        // Fetch the active book borrow record
        BookBorrow bookBorrow = bookBorrowRepository.findActiveBookBorrow(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found"));

        // Prepare the response DTO with borrow details
        BookBorrowDto.Response response = new BookBorrowDto.Response();
        response.setId(bookBorrow.getId());
        response.setBorrowerId(bookBorrow.getBorrower().getId());
        response.setBookId(bookBorrow.getBook().getId());
        response.setBorrowDate(bookBorrow.getBorrowDate());

        // Calculate expected return date (2 weeks from borrow date)
        LocalDateTime expectedReturnDate = bookBorrow.getBorrowDate().plusWeeks(2);
        response.setExpectedReturnDate(expectedReturnDate);

        // Check if the book is overdue
        boolean isOverdue = LocalDateTime.now().isAfter(expectedReturnDate);
        response.setOverdue(isOverdue);

        return response;
    }
}
