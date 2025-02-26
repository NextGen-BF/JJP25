package com.blankfactor.auth.entity.dto.responses;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResponse implements Serializable {

    private String email;

    private Boolean enabled;

}
