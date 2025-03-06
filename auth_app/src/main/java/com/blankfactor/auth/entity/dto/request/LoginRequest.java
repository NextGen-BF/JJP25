package com.blankfactor.auth.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    private String loginIdentifier;
    private String password;
}
