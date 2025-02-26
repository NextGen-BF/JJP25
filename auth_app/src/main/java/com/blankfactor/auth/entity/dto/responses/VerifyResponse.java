package com.blankfactor.auth.entity.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResponse implements Serializable {

    private String email;

    private Boolean enabled;

}
