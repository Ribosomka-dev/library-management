package com.library.librarymanagement.service;

import com.library.librarymanagement.dto.BookRequest;
import com.library.librarymanagement.dto.BookResponse;
import com.library.librarymanagement.dto.PageResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.exception.ResourceNotFoundException;
import com.library.librarymanagement.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        log.info("Получение списка всех книг");
        return bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        log.info("Поиск книги по id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Книга с id {} не найдена", id);
                    return new ResourceNotFoundException("Книга не найдена");
                });
        return toResponse(book);
    }
    @Transactional
    public BookResponse create(BookRequest request) {
        log.info("Создание новой книги: {}", request.getTitle());
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        Book saved = bookRepository.save(book);
        log.debug("Книга успешно создана с id: {}", saved.getId());
        return toResponse(saved);
    }
    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        log.info("Обновление книги с id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Книга с id {} не найдена", id);
                    return new ResourceNotFoundException("Книга не найдена");
                });
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setTotalCopies(request.getTotalCopies());
        log.debug("Книга с id {} успешно обновлена", id);
        return toResponse(bookRepository.save(book));
    }
    @Transactional
    public void delete(Long id) {
        log.info("Удаление книги с id: {}", id);
        bookRepository.deleteById(id);
        log.debug("Книга с id {} успешно удалена", id);
    }
    @Transactional(readOnly = true)
    public List<BookResponse> search(String title, String author) {
        log.info("Поиск книг по title: {}, author: {}", title, author);
        if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        if (author != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        return findAll();
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findAll(pageable);
        return new PageResponse<>(
                books.getContent()
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages()
        );
    }

}
