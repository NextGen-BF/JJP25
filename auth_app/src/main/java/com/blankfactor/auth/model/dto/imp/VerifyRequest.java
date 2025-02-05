package com.blankfactor.auth.model.dto.imp;

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

    @NotBlank(message = "{email.blank}")
    @Email(message = "{email.format}")
    private String email;

    @NotBlank(message = "{verificationCode.blank}")
    @Size(min = 6, max = 6, message = "{verificationCode.size}")
    private String verificationCode;
}
