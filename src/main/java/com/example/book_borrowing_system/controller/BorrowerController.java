package com.example.book_borrowing_system.controller;

import com.example.book_borrowing_system.dto.BorrowerDto;
import com.example.book_borrowing_system.model.Borrower;
import com.example.book_borrowing_system.service.BorrowerService;
import com.example.book_borrowing_system.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {
    private final BorrowerService borrowerService;

    /**
     * Registers a new borrower in the system.
     * Accepts the borrower's name and email in the request body, and returns the registered borrower's details.
     *
     * @param request The registration request containing the borrower's name and email.
     * @return A response containing the registration status and the borrower's details.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BorrowerDto.Response>> registerBorrower(
            @Valid @RequestBody BorrowerDto.RegisterRequest request) {
        Borrower borrower = borrowerService.registerBorrower(request.getName(), request.getEmail());
        return ResponseEntity.ok(ApiResponse.<BorrowerDto.Response>builder()
                .status("SUCCESS")
                .message("Borrower registered successfully")
                .data(convertToResponse(borrower))
                .build());
    }

    /**
     * Retrieves a list of all borrowers in the system.
     *
     * @return A response containing a list of all borrowers' details.
     */
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowerDto.Response>>> getAllBorrowers() {
        List<BorrowerDto.Response> borrowers = borrowerService.getAllBorrowers().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<BorrowerDto.Response>>builder()
                .status("SUCCESS")
                .message("Borrowers retrieved successfully")
                .data(borrowers)
                .build());
    }

    /**
     * Retrieves a borrower by their ID.
     *
     * @param id The ID of the borrower to retrieve.
     * @return A response containing the borrower's details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowerDto.Response>> getBorrowerById(
            @PathVariable long id) {
        Borrower borrower = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(ApiResponse.<BorrowerDto.Response>builder()
                .status("SUCCESS")
                .message("Borrower retrieved successfully")
                .data(convertToResponse(borrower))
                .build());
    }

    /**
     * Converts a Borrower object into a response DTO.
     *
     * @param borrower The Borrower object to convert.
     * @return The BorrowerDto.Response containing the borrower's details.
     */
    private BorrowerDto.Response convertToResponse(Borrower borrower) {
        BorrowerDto.Response response = new BorrowerDto.Response();
        response.setId(borrower.getId());
        response.setName(borrower.getName());
        response.setEmail(borrower.getEmail());
        return response;
    }
}