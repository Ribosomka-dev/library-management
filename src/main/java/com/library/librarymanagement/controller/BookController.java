package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.BookRequest;
import com.library.librarymanagement.dto.BookResponse;
import com.library.librarymanagement.dto.PageResponse;
import com.library.librarymanagement.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.findAll();
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id,
                               @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @GetMapping("/search")
    public List<BookResponse> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        return bookService.search(title, author);
    }
    @GetMapping("/paged")
    public PageResponse<BookResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return bookService.findAllPaged(page, size);
    }

}
