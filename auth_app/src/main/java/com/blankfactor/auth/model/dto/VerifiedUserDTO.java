package com.blankfactor.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifiedUserDTO {

    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Email must be in valid format.")
    private String email;

    @NotBlank
    @Size(min = 6, max = 6, message = "Provide valid verification code.")
    private String verificationCode;

}