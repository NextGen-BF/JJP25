package com.blankfactor.auth.entity.dto.incoming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
public class LoginRequest {

    private String loginIdentifier;
    private String password;
}
