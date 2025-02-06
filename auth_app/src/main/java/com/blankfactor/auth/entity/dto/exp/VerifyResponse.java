package com.blankfactor.auth.entity.dto.exp;

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
public class VerifyResponse implements Serializable {

    private String email;

    private boolean enabled;

    private String verificationCode;

    private LocalDateTime verificationCodeExpiresAt;

}
