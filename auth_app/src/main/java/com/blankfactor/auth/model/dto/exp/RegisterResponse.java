package com.blankfactor.auth.model.dto.exp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse implements Serializable {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private LocalDateTime birthDate;

    private boolean enabled;

    private String verificationCode;

    private LocalDateTime verificationCodeExpiresAt;

}
