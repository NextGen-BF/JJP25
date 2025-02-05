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

    @NotBlank(message = "{email.blank}")
    @Email(message = "{email.format}")
    private String email;

    @NotBlank(message = "{password.blank}")
    @Size(min = 8, max = 25, message = "{password.size}")
    private String password;

    @NotBlank(message = "{firstName.blank}")
    @Size(min = 2, message = "{firstName.size}")
    private String firstName;

    @NotBlank(message = "{lastName.blank}")
    @Size(min = 2, message = "{lastName.size}")
    private String lastName;

    @NotNull(message = "{birthDate.null}")
    private LocalDateTime birthDate;

}
