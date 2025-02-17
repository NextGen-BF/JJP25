package com.blankfactor.auth.entity.dto.exp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse implements Serializable {

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private Boolean enabled;

    private Set<String> roles;

}
