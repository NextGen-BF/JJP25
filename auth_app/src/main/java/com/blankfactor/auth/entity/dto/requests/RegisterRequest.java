package com.blankfactor.auth.entity.dto.requests;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String USERNAME_REGEX = "^(?=[a-zA-Z]*[a-zA-Z]{4,})[a-zA-Z0-9_-]{4,25}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,25}$";

    @NotNull(message = "{email.required}")
    @Pattern(regexp = EMAIL_REGEX, message = "{email.invalidFormat}")
    private String email;

    @NotNull(message = "{password.required}")
    @Pattern(regexp = PASSWORD_REGEX, message = "{password.pattern}")
    private String password;

    @NotNull(message = "{confirmPassword.required}")
    @Size(min = 8, max = 25, message = "{confirmPassword.length}")
    private String confirmPassword;

    @NotNull(message = "{username.required}")
    @Size(max = 25, message = "{username.length}")
    @Pattern(regexp = USERNAME_REGEX, message = "{username.pattern}")
    private String username;

    @NotNull(message = "{firstName.required}")
    @Size(min = 2, max = 20, message = "{firstName.length}")
    private String firstName;

    @NotNull(message = "{lastName.required}")
    @Size(min = 2, max = 20, message = "{lastName.length}")
    private String lastName;

    @NotNull(message = "{birthDate.required}")
    private LocalDateTime birthDate;

    @NotNull(message = "{role.required}")
    @Pattern(regexp = "^(attendee|organiser)$", message = "{role.invalidFormat}")
    private String role;

}
