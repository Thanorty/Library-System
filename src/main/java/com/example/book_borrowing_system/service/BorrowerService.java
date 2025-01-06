package com.example.book_borrowing_system.service;

import com.example.book_borrowing_system.model.Borrower;
import com.example.book_borrowing_system.repository.BorrowerRepository;
import com.example.book_borrowing_system.exception.ResourceNotFoundException;
import com.example.book_borrowing_system.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowerService {
    private final BorrowerRepository borrowerRepository;

    @Transactional
    public Borrower registerBorrower(String name, String email) {
        // Check if email is provided
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory for registering a borrower.");
        }

        // Check for duplicate email
        if (borrowerRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Register the new borrower
        Borrower borrower = new Borrower();
        borrower.setName(name);
        borrower.setEmail(email);
        return borrowerRepository.save(borrower);
    }


    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    public Borrower getBorrowerById(long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
    }
}