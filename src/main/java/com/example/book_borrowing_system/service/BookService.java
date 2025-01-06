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


    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public BookBorrow borrowBook(Long borrowerId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        if (bookBorrowRepository.findActiveBookBorrow(bookId).isPresent()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        BookBorrow bookBorrow = new BookBorrow();
        bookBorrow.setBorrower(borrower);
        bookBorrow.setBook(book);
        bookBorrow.setBorrowDate(LocalDateTime.now());

        return bookBorrowRepository.save(bookBorrow);
    }

    @Transactional
    public BookBorrow returnBook(Long borrowerId, Long bookId) {
        BookBorrow bookBorrow = bookBorrowRepository.findActiveBookBorrow(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found"));

        if (!bookBorrow.getBorrower().getId().equals(borrowerId)) {
            throw new IllegalStateException("Book was not borrowed by this borrower");
        }

        Book book = bookBorrow.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        bookBorrow.setReturnDate(LocalDateTime.now());
        return bookBorrowRepository.save(bookBorrow);
    }

    public Book getBookByIsbn(String isbn) {
        List<Book> books = bookRepository.findByIsbn(isbn);
        if (books.isEmpty()) {
            throw new ResourceNotFoundException("Book not found with ISBN: " + isbn);
        }
        return books.get(0);
    }

    @Transactional
    public BookBorrowDto.Response getBookBorrowDetails(Long bookId) {
        BookBorrow bookBorrow = bookBorrowRepository.findActiveBookBorrow(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found"));

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