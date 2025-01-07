package com.example.book_borrowing_system.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    // Tests if able to add new book successfully
    void registerBook_Success() throws Exception {
        String request = """
            {
                "isbn": "978-3-18-148410-0",
                "title": "Effective Java",
                "author": "Joshua Bloch"
            }
        """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("978-3-18-148410-0"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"));
    }

    @Test
    @Transactional
    // Tests if able to insert multiple book with same isbn but different author and title
    void registerBook_DuplicateISBNDifferentTitle() throws Exception {
        String request1 = """
            {
                "isbn": "978-3-18-148410-0",
                "title": "Effective Java",
                "author": "Joshua Bloch"
            }
        """;

        String request2 = """
            {
                "isbn": "978-3-18-148410-0",
                "title": "Java Concurrency in Practice",
                "author": "Brian Goetz"
            }
        """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A book with the same ISBN exists but with different title or author. Please enter valid Author and Title."));
    }

    @Test
    @Transactional
    // Tests if can insert multiple books with same isbn, title and author
    void registerBook_MultipleCopies() throws Exception {
        String request = """
            {
                "isbn": "978-3-18-148410-0",
                "title": "Effective Java",
                "author": "Joshua Bloch"
            }
        """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }
}
