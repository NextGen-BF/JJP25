package com.blankfactor.auth.entity.dto.imp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = false)
public class LoginRequest {

    private String loginIdentifier;
    private String password;
}
