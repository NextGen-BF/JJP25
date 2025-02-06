package com.blankfactor.auth.entity.dto.imp;

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

    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String USERNAME_REGEX = "^(?=[a-zA-Z]*[a-zA-Z]{4,})[a-zA-Z0-9_-]{4,25}$";

    @NotBlank(message = "{email.required}")
    @Pattern(regexp = EMAIL_REGEX, message = "{email.invalidFormat}")
    private String email;

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 25, message = "{password.length}")
    private String password;

    @NotBlank(message = "{confirmPassword.required}")
    @Size(min = 8, max = 25, message = "{confirmPassword.length}")
    private String confirmPassword;

    @NotBlank(message = "{username.required}")
    @Size(max = 25, message = "{username.length}")
    @Pattern(regexp = USERNAME_REGEX, message = "{username.pattern}")
    private String username;

    @NotBlank(message = "{firstName.required}")
    @Size(min = 2, message = "{firstName.minLength}")
    private String firstName;

    @NotBlank(message = "{lastName.required}")
    @Size(min = 2, message = "{lastName.minLength}")
    private String lastName;

    @NotNull(message = "{birthDate.required}")
    private LocalDateTime birthDate;

}
