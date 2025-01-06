package com.example.book_borrowing_system.repository;

import com.example.book_borrowing_system.model.BookBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long> {
    @Query("SELECT bb FROM BookBorrow bb WHERE bb.book.id = :bookId AND bb.returnDate IS NULL")
    Optional<BookBorrow> findActiveBookBorrow(@Param("bookId") Long bookId);

    List<BookBorrow> findByBorrowerIdAndReturnDateIsNull(Long borrowerId);
    List<BookBorrow> findByBookId(long bookId);

}

