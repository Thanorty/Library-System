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

    /**
     * Registers a new borrower by validating the provided name and email.
     * It checks if the email is valid and not already registered.
     * @param name the name of the borrower
     * @param email the email of the borrower
     * @return the registered Borrower object
     * @throws IllegalArgumentException if the email is empty or null
     * @throws DuplicateResourceException if the email is already registered
     */
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

    /**
     * Retrieves all borrowers from the repository.
     * @return a list of all borrowers
     */
    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    /**
     * Retrieves a borrower by their ID.
     * If the borrower does not exist, a ResourceNotFoundException is thrown.
     * @param id the ID of the borrower
     * @return the Borrower object
     * @throws ResourceNotFoundException if the borrower with the given ID does not exist
     */
    public Borrower getBorrowerById(long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
    }
}
