package com.example.book_borrowing_system.repository;

import com.example.book_borrowing_system.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<Borrower> findByEmail(String email);
}