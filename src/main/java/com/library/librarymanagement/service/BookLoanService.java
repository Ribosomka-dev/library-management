package com.library.librarymanagement.service;
import com.library.librarymanagement.dto.BookLoanRequest;
import com.library.librarymanagement.dto.BookLoanResponse;
import com.library.librarymanagement.dto.PageResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookLoan;
import com.library.librarymanagement.entity.LoanStatus;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.exception.ResourceNotFoundException;
import com.library.librarymanagement.repository.BookLoanRepository;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    @Transactional
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public BookLoanResponse create(BookLoanRequest request) {
        log.info("Выдача книги id: {} читателю id: {}", request.getBookId(), request.getReaderId());
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> {
                    log.error("Книга с id {} не найдена", request.getBookId());
                    return new ResourceNotFoundException("Книга не найдена");
                });
        Reader reader = readerRepository.findById(request.getReaderId())
                .orElseThrow(() -> {
                    log.error("Читатель с id {} не найден", request.getReaderId());
                    return new ResourceNotFoundException("Читатель не найден");
                });
        if (book.getAvailableCopies() <= 0) {
            log.warn("Нет доступных экземпляров книги id: {}", request.getBookId());
            throw new RuntimeException("Нет доступных экземпляров");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        BookLoan loan = new BookLoan();
        loan.setBook(book);
        loan.setReader(reader);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(request.getDueDate());
        loan.setStatus(LoanStatus.ACTIVE);
        BookLoan saved = bookLoanRepository.save(loan);
        log.debug("Книга успешно выдана, займ id: {}", saved.getId());
        return toResponse(saved);
    }

    @Recover
    public BookLoanResponse recover(Exception ex, BookLoanRequest request) {
        log.error("Все попытки исчерпаны для создания займа: {}", ex.getMessage());
        throw new RuntimeException("Сервис временно недоступен, попробуйте позже");
    }
    @Transactional
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public BookLoanResponse returnBook(Long id) {
        log.info("Возврат книги по займу id: {}", id);
        BookLoan loan = bookLoanRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Займ с id {} не найден", id);
                    return new ResourceNotFoundException("Выдача не найдена");
                });
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.getBook().setAvailableCopies(loan.getBook().getAvailableCopies() + 1);
        bookRepository.save(loan.getBook());
        log.debug("Книга успешно возвращена, займ id: {}", id);
        return toResponse(bookLoanRepository.save(loan));
    }

    @Recover
    public BookLoanResponse recoverReturn(Exception ex, Long id) {
        log.error("Все попытки возврата книги исчерпаны: {}", ex.getMessage());
        throw new RuntimeException("Сервис временно недоступен, попробуйте позже");
    }
    @Transactional(readOnly = true)
    public List<BookLoanResponse> findByReader(Long readerId) {
        log.info("История займов читателя id: {}", readerId);
        return bookLoanRepository.findByReaderId(readerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<BookLoanResponse> findOverdue() {
        log.info("Получение списка просроченных займов");
        return bookLoanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private BookLoanResponse toResponse(BookLoan loan) {
        return new BookLoanResponse(
                loan.getId(),
                loan.getBook().getTitle(),
                loan.getReader().getFirstName() + " " + loan.getReader().getLastName(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
    @Transactional(readOnly = true)
    public PageResponse<BookLoanResponse> findAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookLoan> loans = bookLoanRepository.findAll(pageable);
        return new PageResponse<>(
                loans.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                loans.getNumber(),
                loans.getSize(),
                loans.getTotalElements(),
                loans.getTotalPages()
        );
    }

}
