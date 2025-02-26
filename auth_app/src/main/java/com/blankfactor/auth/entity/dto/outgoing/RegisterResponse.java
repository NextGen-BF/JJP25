package com.blankfactor.auth.entity.dto.outgoing;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse implements Serializable {

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private Boolean enabled;

    private Set<String> roles;

}
