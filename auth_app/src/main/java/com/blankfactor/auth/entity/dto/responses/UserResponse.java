package com.blankfactor.auth.entity.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
}