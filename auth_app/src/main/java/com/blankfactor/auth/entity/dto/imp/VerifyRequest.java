package com.blankfactor.auth.entity.dto.imp;

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
public class VerifyRequest {

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.invalidFormat}")
    private String email;

    @NotBlank(message = "{verificationCode.required}")
    @Size(min = 6, max = 6, message = "{verificationCode.invalid}")
    private String verificationCode;
}
