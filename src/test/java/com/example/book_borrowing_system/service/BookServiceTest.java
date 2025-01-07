package com.example.book_borrowing_system.service;

import com.example.book_borrowing_system.exception.ConflictException;
import com.example.book_borrowing_system.model.*;
import com.example.book_borrowing_system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookBorrowRepository bookBorrowRepository;
    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private Borrower testBorrower;
    private BookBorrow testBookBorrow;

    @BeforeEach
    void setUp() {
        // Creating and initializing a test Book object
        testBook = new Book();
        testBook.setId(1L);
        testBook.setIsbn("1234567890");
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setAvailable(true);

        testBorrower = new Borrower();
        testBorrower.setId(1L);
        testBorrower.setName("Test Borrower");
        testBorrower.setEmail("test@test.com");

        testBookBorrow = new BookBorrow();
        testBookBorrow.setId(1L);
        testBookBorrow.setBook(testBook);
        testBookBorrow.setBorrower(testBorrower);
        testBookBorrow.setBorrowDate(LocalDateTime.now());
    }

    @Test
    // Tests Registering new book with unique ISBN
    void registerBook_Success() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(List.of());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.registerBook("1234567890", "Test Book", "Test Author");

        assertNotNull(result);
        assertEquals(testBook.getIsbn(), result.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    // Tests if able register book with Duplicate ISBN and Different Details
    void registerBook_DuplicateISBNDifferentDetails_ThrowsException() {
        Book existingBook = new Book();
        existingBook.setIsbn("1234567890");
        existingBook.setTitle("Different Title");
        existingBook.setAuthor("Different Author");

        when(bookRepository.findByIsbn("1234567890")).thenReturn(List.of(existingBook));

        assertThrows(ConflictException.class, () ->
                bookService.registerBook("1234567890", "Test Book", "Test Author")
        );
    }

    @Test
    void borrowBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(testBorrower));
        when(bookBorrowRepository.findActiveBookBorrow(1L)).thenReturn(Optional.empty());
        when(bookBorrowRepository.save(any(BookBorrow.class))).thenReturn(testBookBorrow);

        BookBorrow result = bookService.borrowBook(1L, 1L);

        assertNotNull(result);
        assertFalse(testBook.isAvailable());
        verify(bookBorrowRepository).save(any(BookBorrow.class));
    }

    @Test
    void borrowBook_BookNotAvailable_ThrowsException() {
        testBook.setAvailable(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(testBorrower));

        assertThrows(IllegalStateException.class, () ->
                bookService.borrowBook(1L, 1L)
        );
    }

    @Test
    void returnBook_Success() {
        when(bookBorrowRepository.findActiveBookBorrow(1L)).thenReturn(Optional.of(testBookBorrow));
        when(bookBorrowRepository.save(any(BookBorrow.class))).thenReturn(testBookBorrow);

        BookBorrow result = bookService.returnBook(1L, 1L);

        assertNotNull(result);
        assertTrue(testBook.isAvailable());
        assertNotNull(result.getReturnDate());
        verify(bookBorrowRepository).save(any(BookBorrow.class));
    }
}

