package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.BookLoan;
import com.library.librarymanagement.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByReaderId(Long readerId);
    List<BookLoan> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);

}

