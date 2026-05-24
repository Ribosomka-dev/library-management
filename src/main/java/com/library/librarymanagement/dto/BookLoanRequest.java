package com.library.librarymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLoanRequest {
    private Long bookId;
    private Long readerId;
    private LocalDate dueDate;
}
