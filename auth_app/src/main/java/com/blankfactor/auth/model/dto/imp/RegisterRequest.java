package com.blankfactor.auth.model.dto.imp;

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

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.invalidFormat}")
    private String email;

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 25, message = "{password.length}")
    private String password;

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 25, message = "{password.length}")
    private String confirmPassword;

    @NotBlank(message = "{firstName.required}")
    @Size(min = 2, message = "{firstName.minLength}")
    private String firstName;

    @NotBlank(message = "{lastName.required}")
    @Size(min = 2, message = "{lastName.minLength}")
    private String lastName;

    @NotNull(message = "{birthDate.required}")
    private LocalDateTime birthDate;

}
