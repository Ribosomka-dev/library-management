package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.BookLoanRequest;
import com.library.librarymanagement.dto.BookLoanResponse;
import com.library.librarymanagement.dto.PageResponse;
import com.library.librarymanagement.service.BookLoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class BookLoanController {

    private final BookLoanService bookLoanService;

    // POST /api/loans
    @PostMapping
    public BookLoanResponse create(@RequestBody BookLoanRequest request) {
        return bookLoanService.create(request);
    }

    // PUT /api/loans/{id}/return
    @PutMapping("/{id}/return")
    public BookLoanResponse returnBook(@PathVariable Long id) {
        return bookLoanService.returnBook(id);
    }

    // GET /api/loans/reader/{readerId}
    @GetMapping("/reader/{readerId}")
    public List<BookLoanResponse> getByReader(@PathVariable Long readerId) {
        return bookLoanService.findByReader(readerId);
    }

    // GET /api/loans/overdue
    @GetMapping("/overdue")
    public List<BookLoanResponse> getOverdue() {
        return bookLoanService.findOverdue();
    }
    @GetMapping("/paged")
    public PageResponse<BookLoanResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return bookLoanService.findAllPaged(page, size);
    }

}

