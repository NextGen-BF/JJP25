package com.blankfactor.auth.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Email must be in valid format.")
    private String email;

    @NotBlank(message = "Password must not be blank.")
    @Size(min = 8, max = 25, message = "Password must be between 8 and 25 characters.")
    private String password;

    @NotBlank(message = "First name must not be blank.")
    @Size(min = 2, message = "First name must be at least 2 characters.")
    private String firstName;

    @NotBlank(message = "Last name must not be blank.")
    @Size(min = 2, message = "Last name must be at least 2 characters.")
    private String lastName;

    @NotNull
    private LocalDateTime birthDate;

}