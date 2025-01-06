package com.example.book_borrowing_system.service;

import com.example.book_borrowing_system.exception.DuplicateResourceException;
import com.example.book_borrowing_system.exception.ResourceNotFoundException;
import com.example.book_borrowing_system.model.Borrower;
import com.example.book_borrowing_system.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowerServiceTest {
    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerService borrowerService;

    private Borrower testBorrower;

    @BeforeEach
    void setUp() {
        testBorrower = new Borrower();
        testBorrower.setId(1L);
        testBorrower.setName("Test Borrower");
        testBorrower.setEmail("test@test.com");
    }

    @Test
    void registerBorrower_Success() {
        when(borrowerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(testBorrower);

        Borrower result = borrowerService.registerBorrower("Test Borrower", "test@test.com");

        assertNotNull(result);
        assertEquals(testBorrower.getEmail(), result.getEmail());
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    void registerBorrower_DuplicateEmail_ThrowsException() {
        when(borrowerRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testBorrower));

        assertThrows(DuplicateResourceException.class, () ->
                borrowerService.registerBorrower("Test Borrower", "test@test.com")
        );
    }

    @Test
    void getBorrowerById_Success() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(testBorrower));

        Borrower result = borrowerService.getBorrowerById(1L);

        assertNotNull(result);
        assertEquals(testBorrower.getId(), result.getId());
    }

    @Test
    void getBorrowerById_NotFound_ThrowsException() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                borrowerService.getBorrowerById(1L)
        );
    }
}
