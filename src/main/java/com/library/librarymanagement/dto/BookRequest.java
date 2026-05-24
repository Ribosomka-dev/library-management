package com.library.librarymanagement.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookRequest {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotBlank(message = "Автор обязателен")
    private String author;

    @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN должен содержать 10 или 13 цифр")
    private String isbn;

    @Positive(message = "Год должен быть положительным")
    private Integer publicationYear;

    @Positive(message = "Количество должно быть положительным")
    private Integer totalCopies;


}
