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

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.invalidFormat}")
    private String email;

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 25, message = "{password.length}")
    private String password;

    @NotBlank(message = "{confirmPassword.required}")
    @Size(min = 8, max = 25, message = "{confirmPassword.length}")
    private String confirmPassword;

    @NotBlank(message = "{username.required}")
    @Size(max = 25, message = "{username.length}")
    @Pattern(regexp = "^[a-zA-Z](?=(?:[^a-zA-Z]*[a-zA-Z]){3,})(?=(?:\\D*\\d){0,4}$)(?=(?:[^\\W_]*[_-]){0,2}$)[a-zA-Z0-9_-]{3,23}[a-zA-Z0-9]$", message = "{username.regex}")
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
