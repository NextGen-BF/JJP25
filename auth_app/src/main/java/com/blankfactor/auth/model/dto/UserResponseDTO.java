package com.blankfactor.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {

    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
}
