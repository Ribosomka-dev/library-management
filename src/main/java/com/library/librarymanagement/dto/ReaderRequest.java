package com.library.librarymanagement.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email(message = "Некорректный email")
    private String email;

    @NotBlank
    private String phone;


}
