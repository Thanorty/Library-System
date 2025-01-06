package com.example.book_borrowing_system.repository;

import com.example.book_borrowing_system.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
    List<Book> findByIsbn(@Param("isbn") String isbn);
    
}
