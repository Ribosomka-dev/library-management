package com.library.librarymanagement.controller;


import com.library.librarymanagement.dto.PageResponse;
import com.library.librarymanagement.dto.ReaderRequest;
import com.library.librarymanagement.dto.ReaderResponse;

import com.library.librarymanagement.service.ReaderService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping("/{id}")
    public ReaderResponse getById(@PathVariable Long id) {
        return readerService.findById(id);
    }

    @PostMapping
    public ReaderResponse create(@RequestBody ReaderRequest request) {
        return readerService.create(request);
    }

    @GetMapping
    public List<ReaderResponse> getAll() {
        return readerService.findAll();
    }

    @PutMapping("/{id}")
    public ReaderResponse update(@PathVariable Long id,
                                 @RequestBody ReaderRequest request) {
        return readerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
    @GetMapping("/paged")
    public PageResponse<ReaderResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return readerService.findAllPaged(page, size);
    }

    }









